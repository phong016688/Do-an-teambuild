import { createParamDecorator, ExecutionContext } from '@nestjs/common'

// custom decorator
// cách dùng
// bắt buộc có guard
// dùng trong hàm @User('attribute muốn lấy') variable <= là attribute của user
export const User = createParamDecorator(
    (data: string, ctx: ExecutionContext) => {
        const { user } = ctx.switchToHttp().getRequest()
        return data ? user && user[data] : user
    }
)     