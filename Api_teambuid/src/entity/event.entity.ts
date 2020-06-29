import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm'
import * as uuid from 'uuid'
import { ByUser, EventState, UserEvent } from '@utils'
import * as moment from 'moment'

@Entity('events')
export class EventEntity {
    @ObjectIdColumn()
    _id: string

    @Column()
    name: string

    @Column()
    idGroup: string

    @Column()
    users: UserEvent[]

    @Column()
    description: string

    @Column()
    isActive: boolean

    @Column()
    isLocked: boolean

    @Column()
    state: EventState

    @Column()
    createdAt: number

    @Column()
    createdBy: ByUser

    @Column()
    updatedAt: number

    @Column()
    updatedBy: ByUser

    @Column()
    verifiedAt: number

    @Column()
    verifiedBy: ByUser

    @BeforeInsert()
    async b4register() {
        this._id = await uuid.v4()
        this.createdAt = this.createdAt || moment().valueOf()
        this.updatedAt = moment().valueOf()
    }

    constructor(args: Partial<EventEntity>) {
        Object.assign(this, args)
    }
}
