import { Injectable, HttpException, HttpStatus } from '@nestjs/common'
import { VoteType, UserEventState } from '@utils'
import { getMongoRepository } from 'typeorm'
import { VoteEntity, EventEntity, UserEntity } from '@entity'
import * as moment from 'moment'

@Injectable()
export class VoteService {
    async modifyVote(idUser: string, idEvent: string, type: VoteType) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id: idEvent, isActive: true })
            if (!event) throw new HttpException('Event not found or has deleted', HttpStatus.NOT_FOUND)
            if (!(event.users || []).some(item => item.idUser === idUser && item.state === UserEventState.APPROVED))
                throw new HttpException(`You was not joined ${event.name}`, HttpStatus.NOT_FOUND)
            const exist = await getMongoRepository(VoteEntity).findOne({ idUser, idEvent })
            if (exist) {
                exist.type = type
                exist.updatedAt = moment().valueOf()
                const saveVote = await getMongoRepository(VoteEntity).save(exist)
                return !!saveVote
            }
            const newVote = new VoteEntity({
                idEvent,
                idUser,
                type,
                createdAt: moment().valueOf(),
                updatedAt: moment().valueOf()
            })
            const saveVote = await getMongoRepository(VoteEntity).save(newVote)
            return !!saveVote
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getallVoteByEvent(idEvent: string) {
        try {
            const votes = await getMongoRepository(VoteEntity).find({
                where: {
                    idEvent,
                    type: {
                        $ne: VoteType.NONE
                    }
                },
                order: {
                    createdAt: 'DESC'
                }
            })
            const users = await getMongoRepository(UserEntity).find({
                where: {
                    _id: {
                        $in: votes.map(item => item.idUser)
                    }
                }
            })
            const usersNameMap = new Map(users.map(item => [item._id, item.name]))
            votes.forEach(item => {
                item['user'] = usersNameMap.get(item.idUser)
            })
            return votes
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
