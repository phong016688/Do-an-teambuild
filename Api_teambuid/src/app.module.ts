import { Module } from '@nestjs/common'
import { AppController } from './app.controller'
import { AppService } from './app.service'
import { UserModule } from './user/user.module'
import { AuthModule } from './auth/auth.module'
import { TypeOrmModule } from '@nestjs/typeorm'
import { join } from 'path'
import { PermissionModule } from './permission/permission.module'
import { EventModule } from './event/event.module'
import { GroupModule } from './group/group.module';
import { PostModule } from './post/post.module';
import { VoteModule } from './vote/vote.module';
import { FeedbackModule } from './feedback/feedback.module';


@Module({
  imports: [
    UserModule,
    AuthModule,
    TypeOrmModule.forRoot(
      {
        url: 'mongodb+srv://tranvu14:F.U.baby1412@cluster0-sudrv.mongodb.net/teambuilding',
        type: "mongodb",
        entities: [join(__dirname, '**/**.entity{.ts,.js}')],
        synchronize: true,
        useNewUrlParser: true,
        logging: true,
        useUnifiedTopology: true
      }
    ),
    PermissionModule,
    EventModule,
    GroupModule,
    PostModule,
    VoteModule,
    FeedbackModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {
}
