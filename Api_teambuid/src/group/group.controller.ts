import { Controller, Get, UseGuards, Body, Query, Post } from '@nestjs/common'
import { GroupService } from './group.service'
import { Reponse, User, AuthGuard, Roles } from '@common'
import { ADMIN, MANAGER, USER, GroupDTO } from '@utils'
import { AppGateway } from 'app.gateway'

@UseGuards(AuthGuard)
@Controller('group')
export class GroupController {
    constructor(
        private readonly groupService: GroupService,
        private readonly appGateway: AppGateway
    ) { }

    @Get('get-all-group')
    async getAllGroups() {
        const data = await this.groupService.getAllGroups()
        return Reponse(data)
    }

    @Get('group')
    async groupById(@Query('id') id) {
        const data = await this.groupService.groupsById(id)
        return Reponse(data)
    }

    @Roles(ADMIN)
    @Post('add-group')
    async addGroup(@User() user, @Body() input: GroupDTO) {
        const data = await this.groupService.addGroups(input, user)
        return Reponse(data)
    }

    @Roles(ADMIN)
    @Post('update-group')
    async updateGroup(@User() user, @Body() { _id, input }) {
        const data = await this.groupService.updateGroup(_id, input, user)
        this.appGateway.sendAlert(
            `${user.mame || ''} đã chỉnh sửa group bạn tham gia`,
            [_id],
            user._id
        )
        return Reponse(data)
    }

    @Roles(ADMIN)
    @Post('delete-group')
    async deleteGroups(@User() user, @Body() { ids }) {
        const data = await this.groupService.deleteGroups(ids, user)
        this.appGateway.sendAlert(
            `${user.mame || ''} đã xóa group bạn tham gia`,
            ids,
            user._id
        )
        return Reponse(data)
    }

    @Roles(ADMIN, MANAGER, USER)
    @Post('search-groups')
    async searchGroups(@Body() { keyword }) {
        const data = await this.groupService.searchGroup(keyword)
        return Reponse(data)
    }
    @Roles(ADMIN, MANAGER)
    @Post('get-user-in-group')
    async getUserInGroup(@Query('id') idGroup) {
        const data = await this.groupService.getUsersInGroup(idGroup)
        return Reponse(data)
    }
}
