import { CanActivate, ExecutionContext, Injectable, Inject, forwardRef } from '@nestjs/common'
import { Reflector } from '@nestjs/core'
import * as jwt from 'jsonwebtoken'
import { getMongoRepository } from 'typeorm'
import { Permission, UserEntity } from '@entity'
import { ACCESS_TOKEN } from '@utils'

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(
        private readonly reflector: Reflector,
    ) { }
    async canActivate(
        context: ExecutionContext,
    ) {
        try {
            // kiểm tra cái router đó cần có quyền gì => [] <=
            const roles = this.reflector.get<string[]>('roles', context.getHandler())
            const request = context.switchToHttp().getRequest()
            const { authorization } = request.headers
            if (!authorization) return false
            const token = authorization.split(' ')[1]
            const decoded: any = jwt.verify(token, ACCESS_TOKEN)
            const { userId } = decoded
            if (!userId) return false
            const user = await getMongoRepository(UserEntity).findOne({ _id: userId })
            if (!user) return false
            // kiểm tra các router nào cần quyền mới kiểm tra
            if (roles) {
                const permission = await getMongoRepository(Permission).findOne({ _id: user.role })
                if (!permission) return false
                if (!roles.some(item => item === permission.code)) return false
            }
            // lưu vào context để sử dụng lần sau
            delete user.password
            context.switchToHttp().getRequest().user = user
            return true
        } catch (err) {
            return false
        }
        return true
    }
}