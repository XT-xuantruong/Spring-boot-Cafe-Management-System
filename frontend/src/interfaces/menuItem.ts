export interface MenuItem {
  itemId: number;
  itemName: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  createdAt: string;
  updatedAt: string;
}

export interface MenuItemRequest {
  itemName: string;
  description: string;
  price: number;
}
