import { Module } from '@nestjs/common'
import { PermissionService } from './permission.service'
import { PermissionController } from './permission.controller'
import { TypeOrmModule } from '@nestjs/typeorm'
import { Permission } from '@entity'

@Module({
  imports: [TypeOrmModule.forFeature([Permission])],
  providers: [PermissionService],
  controllers: [PermissionController]
})
export class PermissionModule { }
