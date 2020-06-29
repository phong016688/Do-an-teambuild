import * as jwt from 'jsonwebtoken'
import * as bcrypt from 'bcrypt'
import * as moment from 'moment'
import { getMongoRepository } from "typeorm"
import { HttpException, HttpStatus, Injectable } from "@nestjs/common"
import { ACCESS_TOKEN, AddUserDTO, LoginDTO, UpdateUserDTO, ByUser, UserEventState } from '@utils'
import { UserEntity, GroupsEntity, EventEntity } from '@entity'

@Injectable()
export class UserService {
  //Đăng nhập
  async login(loginDTO: LoginDTO): Promise<any> {
    const { email, password } = loginDTO
    const userExist = await getMongoRepository(UserEntity).findOne({
      email,
      isActive: true,
      isLocked: false
    })
    if (!userExist) {
      throw new HttpException('User does not exist', HttpStatus.NOT_FOUND)
    }
    if (userExist && (await bcrypt.compareSync(password, userExist.password))) {
      const token = jwt.sign(
        { userId: userExist._id },
        ACCESS_TOKEN,
        { expiresIn: '30d' }
      )
      return { "token": token }
    }
    return 'Wrong username or password!'
  }
  //Thêm user
  async addUser(addUserDTO: AddUserDTO, { _id, name }: ByUser) {
    try {
      const { email } = addUserDTO
      const userExist = await getMongoRepository(UserEntity).findOne({ email, isActive: true })
      if (userExist) throw new HttpException('User exits', HttpStatus.CONFLICT)
      const newUser = new UserEntity({
        ...addUserDTO,
        isActive: true,
        isLocked: false,
        createdBy: {
          _id,
          name
        }
      })
      const saveUser = await getMongoRepository(UserEntity).save(newUser)
      return !!saveUser
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  //Lấy danh sách user
  async getAllUser() {
    try {
      const users = await getMongoRepository(UserEntity)
        .aggregate([
          {
            $match: {
              isActive: true,
              email: { $ne: 'admin' }
            }
          },
          {
            $lookup: {
              from: 'groups',
              localField: 'idGroup',
              foreignField: '_id',
              as: 'group'
            }
          },
          {
            $unwind: {
              path: '$group',
              preserveNullAndEmptyArrays: true
            }
          },
          {
            $sort: {
              isLocked: -1,
              createdAt: -1
            }
          }
        ])
        .toArray()
      users.forEach(item => {
        delete item.password
      })
      return users
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  //Lấy profile
  async getProfile(_id: string) {
    try {
      const user = await getMongoRepository(UserEntity).findOne({
        _id,
        isActive: true
      })
      if (!user) throw new HttpException("User not found", HttpStatus.NOT_FOUND)
      delete user.password
      return user
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async getUserById(_id: string) {
    try {
      const [user] = await getMongoRepository(UserEntity)
        .aggregate([
          {
            $match: {
              _id
            }
          },
          {
            $lookup: {
              from: 'permissions',
              localField: 'role',
              foreignField: '_id',
              as: 'permission'
            }
          },
          {
            $unwind: {
              path: '$permission',
              preserveNullAndEmptyArrays: true
            }
          },
          {
            $lookup: {
              from: 'groups',
              localField: 'idGroup',
              foreignField: '_id',
              as: 'group'
            }
          },
          {
            $unwind: {
              path: '$group',
              preserveNullAndEmptyArrays: true
            }
          }
        ])
        .toArray()
      const events = await getMongoRepository(EventEntity).find({
        where: {
          users: {
            $elemMatch: {
              idUser: _id
            }
          }
        }
      })
      user['events'] = events.map(item => {
        if (Array.isArray(item.users)) {
          const users = item.users.filter(user => user.state === UserEventState.APPROVED)
          item.users = users
        }
        return item
      })
      delete user.password
      return user
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async deletedUser(ids: string[], { _id, name }: ByUser) {
    try {
      const updateMany = await getMongoRepository(UserEntity).updateMany(
        { _id: { $in: ids } },
        {
          $set: {
            isActive: false,
            updatedAt: moment().valueOf(),
            updatedBy: {
              _id,
              name
            }
          }
        }
      )
      return updateMany.result.nModified > 0
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async updateUser(_id: string, input: UpdateUserDTO, { _id: idUser, name }: ByUser) {
    try {
      const update = await getMongoRepository(UserEntity).updateOne(
        {
          _id,
          isActive: true
        },
        {
          $set: {
            ...input,
            updatedAt: moment().valueOf(),
            updatedBy: {
              _id: idUser,
              name
            }
          }
        }
      )
      return !!update.result.ok
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async lockAndUnlockUser(_id: string, { _id: idUser, name }: ByUser) {
    try {
      const userExist = await getMongoRepository(UserEntity).findOne({
        _id,
        isActive: true
      })
      if (!userExist) throw new HttpException('User not found', HttpStatus.NOT_FOUND)
      userExist.isLocked = !userExist.isLocked
      userExist.updatedAt = moment().valueOf()
      userExist.updatedBy = {
        _id: idUser,
        name
      }
      const saveUSer = await getMongoRepository(UserEntity).save(userExist)
      return !!saveUSer
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async searchUser(keyword: string, searchBy: string = 'all') {
    try {
      let condition: any = {
        isActive: true
      }
      if (searchBy === 'email') {
        condition.email = { $regex: keyword, $options: 'siu' }
      } else if (searchBy === 'name') {
        condition.name = { $regex: keyword, $options: 'siu' }
      } else {
        condition.$or = [
          { email: { $regex: keyword, $options: 'siu' } },
          { name: { $regex: keyword, $options: 'siu' } }
        ]
      }
      const users = await getMongoRepository(UserEntity)
        .aggregate([
          {
            $match: condition
          },
          {
            $limit: 20
          }
        ])
        .toArray()
      return users
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async eventsOfGroupByUserId(_id: string) {
    try {
      const user = await getMongoRepository(UserEntity).findOne({ _id })
      if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND)
      const [group, events] = await Promise.all<
        GroupsEntity,
        EventEntity[]
      >([
        getMongoRepository(GroupsEntity).findOne({ _id: user.idGroup }),
        getMongoRepository(EventEntity).find({
          where: {
            idGroup: user.idGroup
          },
          order: {
            createdAt: 'DESC'
          }
        })
      ])
      if (group) {
        group['events'] = events
        user['group'] = group
      }
      return user
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async requestJoinEvent(idUser: string, idEvent: string) {
    try {
      const event = await getMongoRepository(EventEntity).findOne({ _id: idEvent })
      if (!event) throw new HttpException('Event not found', HttpStatus.NOT_FOUND)
      const { users = [] } = event
      const idsUser = [...users.map(item => item.idUser)]
      if (idsUser.some(item => item === idUser))
        throw new HttpException('User has already requested this event', HttpStatus.CONFLICT)
      users.push({
        idUser,
        state: UserEventState.REQUESTED
      })
      event.users = users
      event.updatedAt = moment().valueOf()
      const saveEvent = await getMongoRepository(EventEntity).save(event)
      return !!saveEvent
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
}