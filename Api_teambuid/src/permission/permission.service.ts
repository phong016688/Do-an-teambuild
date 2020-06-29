import { Injectable, HttpException, HttpStatus } from '@nestjs/common'
import { getMongoRepository } from 'typeorm'
import { Permission, UserEntity } from '@entity'

@Injectable()
export class PermissionService {
  async permissions() {
    try {
      const permissions = await getMongoRepository(Permission).find({})
      return permissions
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async permission(_id: string) {
    try {
      const permission = await getMongoRepository(Permission).find({ _id })
      return permission
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
  async permissionByUserId(idUser: string) {
    try {
      const user = await getMongoRepository(UserEntity).findOne({ _id: idUser })
      if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND)
      const permission = await getMongoRepository(Permission).findOne({ _id: user.role })
      user['permission'] = permission
      return permission
    } catch (error) {
      throw new HttpException(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
}
