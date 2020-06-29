export interface ByUser {
  _id: string
  name: string
}
export interface AddUserDTO {
  name: string
  password: string
  gender: Gender
  email: string
  phoneNumber: string
  avatar: string
  birthday: string
  role: string
  idGroup: string
}
export interface LoginDTO {
  email: string
  password: string
}

export interface UpdateUserDTO {
  name: string
  password: string
  phoneNumber: string
  idGroup: string
  avatar: string
  birthday: string
  role: string
  gender: Gender
}
export interface AddEventDTO {
  name: string
  idGroup: string
  users: UserEvent[]
  description: string
}
export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}
export interface UserEvent {
  idUser: string
  state: UserEventState
}
export enum UserEventState {
  APPROVED = 'APPROVED',
  REQUESTED = 'REQUESTED',
  CANCELLED = 'CANCELLED'
}

export enum EventState {
  COMPLETED = 'COMPLETED',
  PROCESSING = 'PROCESSING',
  CANCELLED = 'CANCELLED'
}
export enum VoteType {
  LIKE = 'LIKE',
  DISLIKE = 'DISLIKE',
  NONE = 'NONE'
}
export interface GroupDTO {
  name: string
  avatar: string
  description: string
}
export interface FeedbackDTO {
  idEvent: string
  content: string
}