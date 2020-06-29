import {
  SubscribeMessage,
  WebSocketGateway,
  OnGatewayInit,
  OnGatewayConnection,
  OnGatewayDisconnect,
  WebSocketServer
} from '@nestjs/websockets'
import { Logger } from '@nestjs/common'
import { Socket, Server } from 'socket.io'
import * as jwt from 'jsonwebtoken'
import { ACCESS_TOKEN } from '@utils'
import { getMongoRepository } from 'typeorm'
import { UserEntity, EventEntity } from '@entity'

@WebSocketGateway({ transport: ['websocket'] })
export class AppGateway implements OnGatewayInit, OnGatewayConnection, OnGatewayDisconnect {
  private users = []
  @WebSocketServer()
  private ws: Server
  private logger: Logger = new Logger('AppGateway')

  afterInit(server: any) {
    this.logger.log('Initialized!')
  }

  handleDisconnect(client: Socket) {
    this.logger.log(`Client Disconnect: ${client.id}`)
    const users = this.users.filter(item => item.idClient !== client.id)
    this.users = users
  }

  async disconectServer(client: Socket) {
    client.disconnect()
    this.logger.log(`Client Disconnect: ${client.id}`)
    const users = this.users.filter(item => item.idClient !== client.id)
    this.users = users
  }
  // kiểm tra ai đang sub
  // nếu k có quyền thì disconnect
  async handleConnection(client: Socket, ...args: any[]) {
    try {
      this.logger.log(`Client Connected: ${client.id}`)
      const { authorization } = client.handshake.query
      if (!authorization) this.disconectServer(client)
      const token = authorization.split(' ')[1]
      const decoded: any = jwt.verify(token, ACCESS_TOKEN)
      const { userId } = decoded
      if (!userId) return this.disconectServer(client)
      const user = await getMongoRepository(UserEntity).findOne({ _id: userId })
      if (!user) return this.disconectServer(client)
      const events = await getMongoRepository(EventEntity).find({
        where: {
          users: {
            $elemMatch: {
              idUser: userId
            }
          }
        }
      })

      // Lưu lại danh sách những ai đang sub
      user['idAlert'] = new Set([user.idGroup].concat(events.map(item => item._id)))
      this.users.push({
        idClient: client.id,
        user
      })
    } catch (error) {
      this.disconectServer(client)
    }
  }
  @SubscribeMessage('msgFromSever')
  sendAlert(message: string, data: string[] = [], idUser) {
    this.users.map(item => {
      // Điều kiện nhận thông báo
      // 1, không phải người tạo ra thông báo
      // 2, có tham gia vào group hoặc event đang nhận thông báo
      if (data.some(id => item?.user?.idAlert?.has(id) || false)) {
        const client = this.ws.sockets.connected[item.idClient]
        if (client) {
          client.emit('msgFromSever', { data: { message } })
        }
      }
    })
  }
}
