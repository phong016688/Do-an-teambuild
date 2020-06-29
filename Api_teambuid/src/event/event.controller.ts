import { Controller, Get, Query, UseGuards, Post, Body } from '@nestjs/common';
import { EventService } from './event.service';
import { Roles, Reponse, AuthGuard, User } from '@common';
import { ADMIN, MANAGER, AddEventDTO } from '@utils';
import { AppGateway } from 'app.gateway';

@UseGuards(AuthGuard)
@Controller('event')
export class EventController {
  constructor(
      private readonly eventService: EventService,
      private readonly appGateway: AppGateway
  ) {
  }

  @Roles(ADMIN, MANAGER)
  @Get('get-all-event')
  async getAllEvent() {
    const data = await this.eventService.getAllEvent()
    return Reponse(data)
  }

  @Roles(ADMIN, MANAGER)
  @Get('get-event-by-id')
  async getEventById(@Query('id') id: string) {
    const data = await this.eventService.getEventById(id)
    return Reponse(data)
  }

  @Roles(ADMIN, MANAGER)
  @Post('lock-and-unlock-event')
  async lockAndUnlockEvent(@Body() { _id }, @User() user) {
    const data = await this.eventService.lockAndUnlockEvent(_id, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã ${data.isLocked ? 'Khóa' : 'Mở khóa'} sự kiện bạn tham gia`,
        [_id],
        user._id
    )
    return Reponse(!!data)
  }

  @Roles(MANAGER)
  @Post('add-event')
  async addEvent(@Body() input: AddEventDTO, @User() user) {
    const data = await this.eventService.addEvent(input, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã thêm sự kiện ${data.name}`,
        [input.idGroup],
        user._id
    )
    return Reponse(!!data)
  }

  @Roles(MANAGER)
  @Post('update-event')
  async updateEvent(@Body() { _id, input }, @User() user) {
    const data = await this.eventService.updateEvent(_id, input, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã sửa sự kiện ${data.name} mà bạn tham gia`,
        [_id],
        user._id
    )
    return Reponse(!!data)
  }

  @Roles(MANAGER)
  @Post('delete-event')
  async deleteEvent(@Body()  { _id }, @User() user) {
    const data = await this.eventService.deleteEvent([_id], user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã xóa sự kiện bạn đang tham gia`,
        [_id],
        user._id
    )
    return Reponse(data)
  }

  @Roles(MANAGER)
  @Post('add-user-to-event')
  async addUserToEvent(@Body() { _id, idUser }, @User() user) {
    const data = await this.eventService.addUserToEvent(_id, idUser, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã thêm người vào sự kiện bạn đang tham gia`,
        [_id],
        user._id
    )
    return Reponse(data)
  }

  @Roles(MANAGER)
  @Post('remove-user-from-event')
  async removeUserFromEvent(@Body() { _id, idUser }, @User() user) {
    const data = await this.eventService.removeUserFromEvent(_id, idUser, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã xóa người ra khỏi sự kiện bạn đang tham gia`,
        [_id],
        user._id
    )
    return Reponse(data)
  }

  @Roles(MANAGER)
  @Post('complete-event')
  async completeEvent(@Body() { _id }, @User() user) {
    const data = await this.eventService.completeEvent(_id, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã đánh dấu ${data.name} đã hoàn thành`,
        [_id],
        user._id
    )
    return Reponse(data)
  }

  @Roles(MANAGER)
  @Post('cancel-event')
  async cancelEvent(@Body() { _id }, @User() user) {
    const data = await this.eventService.cancelEvent(_id, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã đánh dấu ${data.name} đã bị hủy`,
        [_id],
        user._id
    )
    return Reponse(data)
  }

  @Roles(MANAGER)
  @Post('approve-user-request')
  async approveUserRequest(@Body() { idUser, idEvent }, @User() user) {
    const data = await this.eventService.approveUserRequest(idUser, idEvent, user)
    this.appGateway.sendAlert(
        `${user.name || ''} đã thêm 1 người vào sự kiện bạn đang tham gia`,
        [idEvent],
        user._id
    )
    return Reponse(data)
  }

  @Roles(ADMIN, MANAGER)
  @Get('pagination-event')
  async paginationEvent(@Query() { idGroup = null, currentPage = 1, pageSize = 20 }) {
    const data = await this.eventService.paginationEvent(idGroup, currentPage, pageSize)
    return Reponse(data)
  }

  @Get('get-event-by-group')
  async getEventByGroup(@Query('id') id, @User('_id') idUser) {
    const data = await this.eventService.getEventByGroup(id, idUser)
    return Reponse(data)
  }

  @Get('get-event-by-user')
  async getEventByUser(@Query('id') id) {
    const data = await this.eventService.getEventByUserId(id)
    return Reponse(data)
  }
}
