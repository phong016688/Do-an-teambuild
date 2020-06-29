import { Controller, Query, Get, UseGuards } from '@nestjs/common'
import { Roles, Reponse, AuthGuard } from '@common'
import { ADMIN } from '@utils'
import { PermissionService } from './permission.service'

@UseGuards(AuthGuard)
@Controller('permission')
export class PermissionController {
    constructor(
        private readonly permissionService: PermissionService
    ) { }

    @Roles(ADMIN)
    @Get('permissions')
    async permissions() {
        const data = await this.permissionService.permissions()
        return Reponse(data)
    }

    @Roles(ADMIN)
    @Get('permission')
    async permission(@Query('id') id) {
        const data = await this.permissionService.permission(id)
        return Reponse(data)
    }

    @Roles(ADMIN)
    @Get('permission-by-user-id')
    async permissionByUserId(@Query('id') id) {
        const data = await this.permissionService.permissionByUserId(id)
        return Reponse(data)
    }
}
