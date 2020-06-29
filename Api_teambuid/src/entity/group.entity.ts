import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm'
import * as uuid from 'uuid'
import { ByUser } from '@utils'
import * as moment from 'moment'

@Entity('groups')
export class GroupsEntity {
    @ObjectIdColumn()
    _id: string

    @Column()
    name: string

    @Column()
    avatar: string

    @Column()
    description: string

    @Column()
    isActive: boolean

    @Column()
    createdAt: number

    @Column()
    createdBy: ByUser

    @Column()
    updatedAt: number

    @Column()
    updatedBy: ByUser

    @BeforeInsert()
    async b4register() {
        this._id = await uuid.v4()
        this.createdAt = this.createdAt || moment().valueOf()
        this.updatedAt = moment().valueOf()
    }

    constructor(args: Partial<GroupsEntity>) {
        Object.assign(this, args)
    }
}
