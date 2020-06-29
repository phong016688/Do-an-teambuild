import { Module } from '@nestjs/common';
import { VoteController } from './vote.controller';
import { VoteService } from './vote.service'
import { TypeOrmModule } from '@nestjs/typeorm'
import { VoteEntity } from 'entity/vote.entity'

@Module({
  imports: [TypeOrmModule.forFeature([VoteEntity])],
  controllers: [VoteController],
  providers: [VoteService]
})
export class VoteModule { }
