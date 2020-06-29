import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm'
import * as uuid from 'uuid'
import { VoteType } from '@utils'
import * as moment from 'moment'

@Entity('votes')
export class VoteEntity {
	@ObjectIdColumn()
	_id: string

	@Column()
	idUser: string

	@Column()
	idEvent: string

	@Column()
	type: VoteType

	@Column()
  createdAt: number

  @Column()
  updatedAt: number

	@BeforeInsert()
	async b4register() {
		this._id = await uuid.v4()
    this.createdAt = this.createdAt || moment().valueOf()
    this.createdAt = moment().valueOf()
	}

	constructor(args: Partial<VoteEntity>) {
		Object.assign(this, args)
	}
}
