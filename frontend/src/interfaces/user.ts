export interface User {
  id: number;
  email: string;
  name: string;
  avatarUrl: string | null;
  phone: string | null;
  address: string | null;
  role: string;
  createdAt: string;
  updatedAt: string;
}

export interface UserCredentials {
  name?: string;
  email: string;
  password: string;
}

export interface UserUpdate {
  name?: string;
  avatarUrl?: string;
  phone?: string | null;
  address?: string | null;
  role?: string;
  password?: string;
}

export interface UserRequest {
  email: string;
  password: string;
  name: string;
  phone?: string | null;
  address?: string | null;
  avatarUrl?: string | null;
  role?: string;
}
