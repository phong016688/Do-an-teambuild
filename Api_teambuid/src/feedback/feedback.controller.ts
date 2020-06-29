import { Controller, UseGuards, Get, Query, Post, Body } from '@nestjs/common'
import { AuthGuard, Reponse, User, Roles } from '@common'
import { FeedbackService } from './feedback.service'
import { FeedbackDTO, ADMIN, MANAGER, USER } from '@utils'
import { AppGateway } from 'app.gateway'

@UseGuards(AuthGuard)
@Controller('feedback')
export class FeedbackController {
    constructor(
        private readonly feedbackService: FeedbackService,
        private readonly appGateway: AppGateway
    ) { }

    @Roles(ADMIN, MANAGER, USER)
    @Get('get-all-feedback')
    async getAllFeedback() {
        const data = await this.feedbackService.getAllFeedback()
        return Reponse(data)
    }

    @Roles(ADMIN, MANAGER, USER)
    @Get('feedback-by-event')
    async getFeedbackByEvent(@Query('id') id) {
        const data = await this.feedbackService.getFeedbackByEvent(id)
        return Reponse(data)
    }

    @Roles(ADMIN, MANAGER, USER)
    @Post('add-feedback')
    async addFeedBack(@Body() input: FeedbackDTO, @User() user) {
        const data = await this.feedbackService.addFeedback(input, user)
        this.appGateway.sendAlert(
            `${user.name || ''} đã bình luận ở event mà bạn đang tham gia`,
            [input.idEvent],
            user._id
        )
        return Reponse(data)
    }
}
