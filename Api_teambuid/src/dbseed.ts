import { MongoClient } from 'mongodb'
import * as monent from 'moment'

async function main() {
	console.log('🌱  Database seeder is running')

	const userName = 'tranvu14'
	const password = 'F.U.baby1412'
	const dbName = 'teambuilding'

	const url = `mongodb+srv://${userName}:${password}@cluster0-sudrv.mongodb.net/${dbName}?retryWrites=true&w=majority`
	console.log(`🔗  MONGO_URL: ${url}`)

	const client = new MongoClient(url, {
		useNewUrlParser: true
	})

	try {
		await client.connect()
		console.log('🚀  Server ready')

		const db = client.db(dbName)

		const users = [
			{
				_id: 'c30c0730-be4f-11e9-9f04-f72d443f7ef2',
				email: 'admin',
				name: 'admin',
				role: '0'
			}
		]
		users.map(async item => {
			await db.collection('users').findOneAndUpdate(
				{ _id: item._id },
				{
					$setOnInsert: {
						_id: item._id
					},
					$set: {
						email: item.email,
						name: item.name,
						password:
							'$2b$10$LBWSyma8eNmRYUZi7VUYHef.OeGT2u46nFMKO86Akkmw7jdoE98De',
						role: item.role,
						createdAt: monent().valueOf(),
						updatedAt: monent().valueOf()
					}
				},
				{ upsert: true }
			)
		})

		const permissions = [
			{
				_id: '0',
				code: 'ADMIN',
				description: 'Admin'
			},
			{
				_id: '2',
				code: 'MANAGER',
				description: 'Người quản lí'
			},
			{
				_id: '1',
				code: 'USER',
				description: 'Người dùng'
			}
		]

		permissions.map(async item => {
			await db.collection('permissions').findOneAndUpdate(
				{ _id: item._id },
				{
					$setOnInsert: {
						_id: item._id
					},
					$set: {
						code: item.code,
						description: item.description,
						createdAt: monent().valueOf(),
						updatedAt: monent().valueOf()
					}
				},
				{ upsert: true }
			)
		})

		client.close()
		console.log('💤  Server off')
	} catch (err) {
		console.log('❌  Server error', err.stack)
	}
}

main()
