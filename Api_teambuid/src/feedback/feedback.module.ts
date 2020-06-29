import { Module } from '@nestjs/common';
import { FeedbackController } from './feedback.controller'
import { FeedbackService } from './feedback.service'
import { TypeOrmModule } from '@nestjs/typeorm'
import { FeedbackEntity } from '@entity'
import { AppGateway } from 'app.gateway';

@Module({
  imports: [TypeOrmModule.forFeature([FeedbackEntity])],
  controllers: [FeedbackController],
  providers: [FeedbackService, AppGateway]
})
export class FeedbackModule { }
