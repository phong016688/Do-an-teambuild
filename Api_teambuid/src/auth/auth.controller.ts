import { Body, Controller, HttpStatus, Post, Res } from '@nestjs/common';
import { UserService } from 'user/user.service';
import { LoginDTO } from '@utils';

@Controller('auth')
export class AuthController {
  constructor(
      private userService: UserService,
  ) { }

  @Post('login')
  async login(@Body() userDTO: LoginDTO) {
    return this.userService.login(userDTO);
  }
}
