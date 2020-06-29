import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm'
import * as uuid from 'uuid'
import { ByUser } from '@utils'
import * as moment from 'moment'

@Entity('permissions')
export class Permission {
  @ObjectIdColumn()
  _id: string

  @Column()
  code: string

  @Column()
  description: string

  @Column()
  createdAt: number

  @Column()
  createdBy: ByUser

  @Column()
  updatedAt: number

  @Column()
  updatedBy: number

  @BeforeInsert()
  async b4register() {
    this._id = await uuid.v4()
    this.createdAt = this.createdAt || moment().valueOf()
    this.updatedAt = moment().valueOf()
  }

  constructor(args: Partial<Permission>) {
    Object.assign(this, args)
  }
}
