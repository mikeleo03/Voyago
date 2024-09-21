// Main Model
export interface User {
    id: string;
    email: string;
    username: string;
    password: string;
    picture: string;
    phone: string;
    role: string;
    status: string;
}

// DTOs
export interface LoginRequest {
    username: string;
    password: string;
}

export interface SignupRequest {
    id: string;
    username: string;
    email: string;
    password: string;
    status: string;
}

export interface UserDTO {
    id: string;
    email: string;
    username: string;
    picture: string;
    phone: string;
    status: string;
}

export interface UserSaveDTO {
    email: string;
    username: string;
    password: string;
    phone: string;
}

export interface UserUpdateDTO {
    email: string;
    username: string;
    phone: string;
    picture: string;
}

export interface UpdatePasswordDTO {
    password: string;
}

// Responses
export interface LoginResponse {
    token: string;
}

export interface SignupResponse {
    status: string;
}