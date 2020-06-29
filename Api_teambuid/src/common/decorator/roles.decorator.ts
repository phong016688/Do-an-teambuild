import { SetMetadata } from '@nestjs/common'

// custom decorator
// cách dùng
// dùng như guard, kiểm tra quyền
// @Roles('quyền 1', 'quyền 2') => dữ liệu được lưu vào Reflector dạng mảng
export const Roles = (...roles: string[]) => SetMetadata('roles', roles)
