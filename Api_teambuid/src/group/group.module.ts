import { Module } from '@nestjs/common'
import { GroupController } from './group.controller'
import { GroupService } from './group.service'
import { TypeOrmModule } from '@nestjs/typeorm'
import { GroupsEntity } from '@entity'
import { AppGateway } from 'app.gateway'

@Module({
    imports: [TypeOrmModule.forFeature([GroupsEntity])],
    controllers: [GroupController],
    providers: [GroupService, AppGateway]
})
export class GroupModule { }
