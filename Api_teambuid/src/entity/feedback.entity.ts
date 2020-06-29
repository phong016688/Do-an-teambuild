import { Entity, ObjectIdColumn, Column, BeforeInsert } from 'typeorm'
import * as uuid from 'uuid'
import * as moment from 'moment'

@Entity('feedback')
export class FeedbackEntity {
	@ObjectIdColumn()
	_id: string

	@Column()
	idEvent: string

	@Column()
	idUser: string

	@Column()
	content: string

	@Column()
	createdAt: number

	@BeforeInsert()
	async b4register() {
		this._id = await uuid.v4()
		this.createdAt = this.createdAt || moment().valueOf()
	}

	constructor(args: Partial<FeedbackEntity>) {
		Object.assign(this, args)
	}
}
