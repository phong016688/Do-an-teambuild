import { Controller, Get, Req, UnauthorizedException, HttpException, HttpStatus, UseGuards, Res } from "@nestjs/common";
import * as jwt from 'jsonwebtoken'
import { Request } from "express";
import { AuthGuard, Roles } from "@common"
import { ADMIN } from "@utils"

@Controller('post')
export class PostController {
    @Get('public')
    async public() {
        return 'public content'
    }

    @Get('protected')
    @UseGuards(AuthGuard)
    @Roles(ADMIN)
    async protect(@Req() req: Request) {
        return 'ok'
    }
}