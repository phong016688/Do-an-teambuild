import { Injectable, HttpException, HttpStatus } from '@nestjs/common'
import { getMongoRepository } from 'typeorm'
import { GroupsEntity, EventEntity, UserEntity } from '@entity'
import * as moment from 'moment'
import { GroupDTO } from '@utils'

@Injectable()
export class GroupService {
    async getAllGroups() {
        try {
            const groups = await getMongoRepository(GroupsEntity).find({
                where: {
                    isActive: true
                },
                order: {
                    createdAt: 'DESC'
                }
            })
            const data = []
            for (const group of groups) {
                const [eventCount, userCount] = await Promise.all<number, number>([
                    getMongoRepository(EventEntity).count({
                        idGroup: group._id,
                        isActive: true
                    }),
                    getMongoRepository(UserEntity).count({
                        idGroup: group._id,
                        isActive: true
                    })
                ])
                group['numberOfUser'] = userCount
                group['numberOfEvent'] = eventCount

                data.push(group)
            }
            return data
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async groupsById(_id: string) {
        try {
            const group = await getMongoRepository(GroupsEntity).findOne({
                _id,
                isActive: true
            })
            if (!group) throw new HttpException('Group not found', HttpStatus.NOT_FOUND)
            // const [eventsByGroup, usersByGroup] = await Promise.all<
            //     EventEntity[],
            //     UserEntity[]
            // >([
            //     getMongoRepository(EventEntity).find({
            //         idGroup: group._id,
            //         isActive: true
            //     }),
            //     getMongoRepository(UserEntity).find({
            //         idGroup: group._id,
            //         isActive: true
            //     })
            // ])
            // group['users'] = usersByGroup.map(item => {
            //     delete item.password
            //     return item
            // })
            // group['events'] = eventsByGroup
            const eventCount = await getMongoRepository(EventEntity).count({
                idGroup: group._id,
                isActive: true
            })
            group['eventCount'] = eventCount
            return group
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async addGroups(input: GroupDTO, { _id: idUser, name: userName }) {
        try {
            const groupExist = await getMongoRepository(GroupsEntity).findOne({ name: input.name, isActive: true })
            if (groupExist) throw new HttpException('Group name has already exist', HttpStatus.CONFLICT)
            const newGroup = new GroupsEntity({
                ...input,
                isActive: true,
                createdAt: moment().valueOf(),
                createdBy: {
                    _id: idUser,
                    name: userName
                }
            })
            const saveGroup = await getMongoRepository(GroupsEntity).save(newGroup)
            return !!saveGroup
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async updateGroup(_id: string, input: GroupDTO, { _id: idUser, name: userName }) {
        try {
            const groupExist = await getMongoRepository(GroupsEntity).findOne({
                where: {
                    name: input.name,
                    isActive: true,
                    _id: { $ne: _id }
                }
            })
            if (groupExist) throw new HttpException('Group name has already exist', HttpStatus.CONFLICT)
            const result = await getMongoRepository(GroupsEntity).findOneAndUpdate(
                { _id },
                {
                    $set: {
                        ...input,
                        updatedAt: moment().valueOf(),
                        updatedBy: {
                            _id: idUser,
                            name: userName
                        }
                    }
                }
            )
            return !!result.ok
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async deleteGroups(ids: string[], { _id: idUser, name: userName }) {
        try {
            const updated = await getMongoRepository(GroupsEntity).updateMany(
                { _id: { $in: ids } },
                {
                    $set: {
                        isActive: false,
                        updatedAt: moment().valueOf(),
                        updatedBy: {
                            _id: idUser,
                            name: userName
                        }
                    }
                }
            )
            return !!updated.result.ok
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async searchGroup(keyword: string) {
        try {
            let condition: any = {
                isActive: true,
                name: { $regex: keyword, $options: 'siu' }
            }
            const groups = await getMongoRepository(GroupsEntity)
                .aggregate([
                    {
                        $match: condition
                    },
                    {
                        $limit: 20
                    }
                ])
                .toArray()
            return groups
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
    async getUsersInGroup(idGroup: string) {
        try {
            const user = await getMongoRepository(UserEntity).find({
                where: {
                    idGroup,
                    isActive: true,
                },
                order: {
                    isLocked: -1,
                    createdAt: -1
                }
            })
            return user
        } catch (error) {
            throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
