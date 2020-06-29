import { Injectable, HttpException, HttpStatus } from '@nestjs/common'
import { getMongoRepository } from 'typeorm'
import { EventEntity, FeedbackEntity, UserEntity } from '@entity'
import { FeedbackDTO, UserEventState } from '@utils'

@Injectable()
export class FeedbackService {
    async getFeedbackByEvent(idEvent: string) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id: idEvent, isActive: true })
            if (!event) throw new HttpException('Event not found or has deleted', HttpStatus.NOT_FOUND)
            const feedbacks = await getMongoRepository(FeedbackEntity).find({
                where: {
                    idEvent: event._id
                },
                order: {
                    createdAt: 'DESC'
                }
            })
            const userMap: Map<string, UserEntity> = await new Promise(async rel => {
                const users = await getMongoRepository(UserEntity).find({
                    where: {
                        _id: { $in: [...new Set(feedbacks.map(item => item.idUser))] }
                    }
                })
                rel(new Map(users.map(item => [item._id, item])))
            })
            return feedbacks.map(item => {
                item['user'] = userMap.get(item.idUser)
                return item
            })
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getAllFeedback() {
        try {
            const [eventMap, feedbacks] = await Promise.all<
                Map<string, EventEntity>,
                FeedbackEntity[]
            >([
                new Promise(async rel => {
                    const events = await getMongoRepository(EventEntity).find({
                        order: {
                            createdAt: 'DESC'
                        }
                    })
                    rel(new Map(events.map(item => [item._id, item])))
                }),
                getMongoRepository(FeedbackEntity).find({})
            ])
            feedbacks.forEach(item => {
                item['event'] = eventMap.get(item.idEvent)
            })
            return feedbacks
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async addFeedback(input: FeedbackDTO, { _id }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id: input.idEvent, isActive: true })
            if (!event) throw new HttpException('Event not found or has deleted', HttpStatus.NOT_FOUND)
            if (!(event.users || []).some(item => item.idUser === _id && item.state === UserEventState.APPROVED))
                throw new HttpException(`You was not joined ${event.name}`, HttpStatus.NOT_FOUND)
            const inseted = await getMongoRepository(FeedbackEntity).insertOne(
                new FeedbackEntity({
                    ...input,
                    idUser: _id
                })
            )
            return !!inseted.result.ok
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
