import { Injectable, HttpException, HttpStatus } from '@nestjs/common'
import { getMongoRepository } from 'typeorm'
import { EventEntity, UserEntity, GroupsEntity, FeedbackEntity, VoteEntity } from '@entity'
import * as moment from 'moment'
import { AddEventDTO, EventState, UserEventState, VoteType } from '@utils'

@Injectable()
export class EventService {
    async getAllEvent() {
        try {
            const events = await getMongoRepository(EventEntity).find({
                where: {
                    isActive: true
                },
                order: {
                    createdAt: 'DESC'
                }

            })
            const [
                userMap,
                groupMap
            ] = await Promise.all<
                Map<string, UserEntity>,
                Map<string, GroupsEntity>
            >([
                new Promise(async resolve => {
                    const users = await getMongoRepository(UserEntity).find({})
                    const userMap = new Map(users.map(item => {
                        delete item.password
                        return [item._id, item]
                    }))
                    resolve(userMap)
                }),
                new Promise(async resolve => {
                    const groups = await getMongoRepository(GroupsEntity).find({})
                    const groupMap = new Map(groups.map(item => [item._id, item]))
                    resolve(groupMap)
                })
            ])
            for (const item of events) {
                const [
                    feedbackCount,
                    likeCount,
                    dislikeCount
                ] = await Promise.all<
                    number,
                    number,
                    number
                >([
                    getMongoRepository(FeedbackEntity).count({
                        idEvent: item._id
                    }),
                    getMongoRepository(VoteEntity).count({
                        idEvent: item._id,
                        type: VoteType.LIKE
                    }),
                    getMongoRepository(VoteEntity).count({
                        idEvent: item._id,
                        type: VoteType.DISLIKE
                    })
                ])
                if (Array.isArray(item.users)) {
                    const users = item.users.map(user => {
                        user['user'] = userMap.get(user.idUser)
                        return user
                    })
                    item.users = users
                }
                item['feedbackCount'] = feedbackCount
                item['likeCount'] = likeCount
                item['dislikeCount'] = dislikeCount
                item['group'] = groupMap.get(item.idGroup)
            }
            return events
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getEventById(_id: string, idUser: string = null) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id })
            if (!event) throw new HttpException('Event not found', HttpStatus.NOT_FOUND)
            const [
                users,
                group,
                feedbacks,
                likeCount,
                dislikeCount,
                vote
            ] = await Promise.all<
                UserEntity[],
                GroupsEntity,
                FeedbackEntity[],
                number,
                number,
                VoteEntity
            >([
                getMongoRepository(UserEntity).find({
                    where: {
                        _id: { $in: (event.users || []).map(item => item.idUser) }
                    }
                }),
                getMongoRepository(GroupsEntity).findOne({
                    _id: event.idGroup
                }),
                getMongoRepository(FeedbackEntity).find({
                    idEvent: _id
                }),
                getMongoRepository(VoteEntity).count({
                    idEvent: _id,
                    type: VoteType.LIKE
                }),
                getMongoRepository(VoteEntity).count({
                    idEvent: _id,
                    type: VoteType.DISLIKE
                }),
                getMongoRepository(VoteEntity).findOne({
                    idEvent: _id,
                    idUser
                })
            ])
            if (vote) {
                event['voteOfMe'] = vote
            }
            const userMap = new Map()
            users.forEach(item => {
                delete item.password
                userMap.set(item._id, item)
            })
            event['group'] = group
            event['dislikeCount'] = dislikeCount
            event['likeCount'] = likeCount
            if (Array.isArray(event.users)) {
                const users = event.users.map(user => {
                    user['user'] = userMap.get(user.idUser)
                    return user
                })
                event.users = users
            }
            event['feedbacks'] = feedbacks.map(item => {
                item['user'] = userMap.get(item.idUser)
                return item
            })
            return event
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getEventByGroup(idGroup: string, idUser: string) {
        try {
            const events = await getMongoRepository(EventEntity).find({
                where: {
                    idGroup,
                    isActive: true
                },
                order: {
                    isLocked: "DESC",
                    createdAt: "DESC"
                }
            })
            for (const item of events) {
                const [likeCount, dislikeCount, feedbackCount, likeCurrent] = await Promise.all<number, number, number, VoteEntity>
                    ([
                        getMongoRepository(VoteEntity).count({
                            idEvent: item._id,
                            type: VoteType.LIKE
                        }),
                        getMongoRepository(VoteEntity).count({
                            idEvent: item._id,
                            type: VoteType.DISLIKE
                        }),
                        getMongoRepository(FeedbackEntity).count({
                            idEvent: item._id
                        }),
                        getMongoRepository(VoteEntity).findOne({
                            idEvent: item._id,
                            idUser
                        })
                    ])
                item['likeCurrent'] = likeCurrent
                item['likeCount'] = likeCount
                item['dislikeCount'] = dislikeCount
                item['feedbackCount'] = feedbackCount
            }
            return events
        }
        catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async lockAndUnlockEvent(_id: string, { _id: idUser, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({
                _id,
                isActive: true
            })
            if (!event) throw new HttpException('Event not found', HttpStatus.NOT_FOUND)
            event.isLocked = !event.isLocked
            event.updatedAt = moment().valueOf()
            event.updatedBy = {
                _id: idUser,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async addEvent(input: AddEventDTO, { _id, name }) {
        try {
            const { users = [] } = input
            const eventExist = await getMongoRepository(EventEntity).findOne({
                name: input.name,
                isActive: true,
                idGroup: input.idGroup
            })
            users.push({
                idUser: _id,
                state: UserEventState.APPROVED
            })
            if (eventExist) throw new HttpException('Sự kiện đã tồn tại', HttpStatus.CONFLICT)
            const newEvent = new EventEntity({
                ...input,
                users,
                isActive: true,
                isLocked: false,
                state: EventState.PROCESSING,
                createdAt: moment().valueOf(),
                createdBy: {
                    _id,
                    name
                }
            })
            const saveEvent = await getMongoRepository(EventEntity).save(newEvent)
            return saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async updateEvent(_id: string, input: AddEventDTO, { _id: idUser, name }) {
        try {
            const eventExist = await getMongoRepository(EventEntity).findOne({
                where: {
                    name: input.name,
                    isActive: true,
                    idGroup: input.idGroup,
                    _id: { $ne: _id }
                }
            })
            if (eventExist) throw new HttpException('Sự kiện đã tồn tại', HttpStatus.CONFLICT)
            const updatedEvent = await getMongoRepository(EventEntity).findOneAndUpdate(
                { _id },
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
            return updatedEvent.value
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async deleteEvent(ids: string[], { _id, name }) {
        try {
            const deleted = await getMongoRepository(EventEntity).updateMany(
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
            return !!deleted.result.ok
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async completeEvent(_id: string, { _id: idUser, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({
                _id,
                isActive: true
            })
            if (!event) throw new HttpException('Không tìm thấy sự kiện', HttpStatus.NOT_FOUND)
            if (event.state === EventState.COMPLETED)
                throw new HttpException('Sự kiện đã hoàn thành', HttpStatus.NOT_FOUND)
            event.state = EventState.COMPLETED
            event.verifiedAt = moment().valueOf()
            event.verifiedBy = {
                _id: idUser,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async cancelEvent(_id: string, { _id: idUser, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({
                _id,
                isActive: true
            })
            if (!event) throw new HttpException('Không tìm thấy sự kiện', HttpStatus.NOT_FOUND)
            if (event.state === EventState.CANCELLED)
                throw new HttpException('Sự kiện đã bị hủy', HttpStatus.NOT_FOUND)
            event.state = EventState.CANCELLED
            event.updatedAt = moment().valueOf()
            event.updatedBy = {
                _id: idUser,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async addUserToEvent(_id: string, idUser: string, { _id: idUserUpdate, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id })
            if (!event) throw new HttpException('Không tìm thấy sự kiện', HttpStatus.NOT_FOUND)
            const { users = [] } = event
            const idsUser = [...users.map(item => item.idUser)]
            if (idsUser.some(item => item === idUser))
                throw new HttpException('Người dùng đã yêu cầu tham gia sự kiện này rồi', HttpStatus.CONFLICT)
            users.push({
                idUser,
                state: UserEventState.APPROVED
            })
            event.users = users
            event.updatedAt = moment().valueOf()
            event.updatedBy = {
                _id: idUserUpdate,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return !!saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async removeUserFromEvent(_id: string, idUser: string, { _id: idUserUpdate, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id })
            if (!event) throw new HttpException('Không tìm thấy sự kiện', HttpStatus.NOT_FOUND)
            const { users = [] } = event
            const idsUser = [...users.map(item => item.idUser)]
            if (!idsUser.some(item => item === idUser))
                throw new HttpException('Người dùng chưa tham gia sự kiện này', HttpStatus.CONFLICT)
            const newUsers = users.filter(user => user.idUser !== idUser)
            event.users = newUsers
            event.updatedAt = moment().valueOf()
            event.updatedBy = {
                _id: idUserUpdate,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return !!saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async approveUserRequest(idUser: string, idEvent: string, { _id: idUserUpdate, name }) {
        try {
            const event = await getMongoRepository(EventEntity).findOne({ _id: idEvent })
            if (!event) throw new HttpException('Không tìm thấy sự kiện', HttpStatus.NOT_FOUND)
            const { users = [] } = event
            const idsUser = [...users.map(item => item.idUser)]
            if (!idsUser.some(item => item === idUser))
                throw new HttpException('Người dùng chưa tham gia sự kiện này', HttpStatus.CONFLICT)
            const newUsers = users.map(item => {
                if (item.idUser === idUser) item.state = UserEventState.APPROVED
                return item
            })
            event.users = newUsers
            event.updatedAt = moment().valueOf()
            event.updatedBy = {
                _id: idUserUpdate,
                name
            }
            const saveEvent = await getMongoRepository(EventEntity).save(event)
            return !!saveEvent
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async paginationEvent(idGroup: string = null, currentPage: number = 1, pageSize: number = 20) {
        try {
            const conditon: any = {
                isActive: true
            }
            if (idGroup) {
                conditon.idGroup = idGroup
            }
            const events = await getMongoRepository(EventEntity).find({
                where: conditon,
                order: {
                    createdAt: 'DESC'
                },
                skip: (currentPage - 1) * pageSize,
                take: pageSize
            })
            const [
                userMap,
                groupMap
            ] = await Promise.all<
                Map<string, UserEntity>,
                Map<string, GroupsEntity>
            >([
                new Promise(async rel => {
                    const users = await getMongoRepository(UserEntity).find({})
                    const userMap = new Map(users.map(item => {
                        delete item.password
                        return [item._id, item]
                    }))
                    rel(userMap)
                }),
                new Promise(async rel => {
                    const groups = await getMongoRepository(GroupsEntity).find({})
                    const groupMap = new Map(groups.map(item => [item._id, item]))
                    rel(groupMap)
                })
            ])
            for (const item of events) {
                const [
                    feedbackCount,
                    likeCount,
                    dislikeCount
                ] = await Promise.all<
                    number,
                    number,
                    number
                >([
                    getMongoRepository(FeedbackEntity).count({
                        idEvent: item._id
                    }),
                    getMongoRepository(VoteEntity).count({
                        idEvent: item._id,
                        type: VoteType.LIKE
                    }),
                    getMongoRepository(VoteEntity).count({
                        idEvent: item._id,
                        type: VoteType.DISLIKE
                    })
                ])
                if (Array.isArray(item.users)) {
                    const users = item.users.map(user => {
                        user['user'] = userMap.get(user.idUser)
                        return user
                    })
                    item.users = users
                }
                item['feedbackCount'] = feedbackCount
                item['likeCount'] = likeCount
                item['dislikeCount'] = dislikeCount
                item['group'] = groupMap.get(item.idGroup)
            }
            return events
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getEventByUserId(idUser: string) {
        try {
            const events = await getMongoRepository(EventEntity).find({
                where: {
                    users: {
                        $elemMatch: {
                            idUser
                        }
                    },
                    isActive: true
                },
                order: {
                    isLocked: 'DESC',
                    createdAt: 'DESC'
                }
            })
            for (const item of events) {
                const [likeCount, dislikeCount] = await Promise.all<number, number>
                    ([
                        getMongoRepository(VoteEntity).count({
                            idEvent: item._id,
                            type: VoteType.LIKE
                        }),
                        getMongoRepository(VoteEntity).count({
                            idEvent: item._id,
                            type: VoteType.DISLIKE
                        })
                    ])
                item['likeCount'] = likeCount
                item['dislikeCount'] = dislikeCount
            }
            return events
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
