export interface User {
  id: number;
  email: string;
  name: string;
  avatar_url: string;
  phone: string;
  address: string;
  role: string;
}

export interface UserCredentials {
  name?: string;
  email: string;
  password: string;
}

export interface UserUpdate {
  name?: string;
  avatar_url?: string;
  phone?: string;
  address?: string;
  role?: string;
}
