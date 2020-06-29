import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm';
import * as uuid from 'uuid'
import * as bcrypt from 'bcrypt';
import { ByUser, Gender } from '@utils';
import * as moment from 'moment'

@Entity('users')
export class UserEntity {
    @ObjectIdColumn()
    _id: string

    @Column()
    name: string

    @Column()
    password: string

    @Column()
    email: string

    @Column()
    phoneNumber: string

    @Column()
    avatar: string

    @Column()
    idGroup: string

    @Column()
    birthday: string

    @Column()
    gender: Gender

    @Column()
    isActive: boolean

    @Column()
    isLocked: boolean

    @Column()
    role: string

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
        this.password = await bcrypt.hash(this.password, 10)
    }

    constructor(args: Partial<UserEntity>) {
        Object.assign(this, args)
    }
}
