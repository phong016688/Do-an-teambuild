import { Module } from '@nestjs/common';
import { EventService } from './event.service';
import { EventController } from './event.controller';
import { AppGateway } from 'app.gateway';

@Module({
  providers: [EventService, AppGateway],
  controllers: [EventController]
})
export class EventModule { }
