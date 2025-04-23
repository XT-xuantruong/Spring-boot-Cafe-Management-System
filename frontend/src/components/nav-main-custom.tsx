import { type LucideIcon } from "lucide-react"

import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { Link } from "react-router-dom"
import { useSelector } from "react-redux"
import { RootState } from "@/stores"
import { User } from "@/interfaces/user"

export function NavMainCustom({
  items,
}: {
  items: {
    title: string
    url: string
    icon?: LucideIcon
  }[]
}) {
  const user = useSelector((state: RootState) => state.auth.user as User | null);
  const filteredNavMain = items.filter((item) => {
    // Hide "Users" item for STAFF role
    if (item.url === '/admin/user' && user?.role === 'STAFF') {
      return false;
    }
    return true;
  });
  return (
    <SidebarGroup>
      <SidebarGroupContent className="flex flex-col gap-2">
        <SidebarMenu>
          {filteredNavMain.map((item) => (
            <SidebarMenuItem key={item.title}>
              <Link to={`${item.url}`}>
                <SidebarMenuButton tooltip={item.title}>
                    {item.icon && <item.icon />}
                    <span>{item.title}</span>
                </SidebarMenuButton>
              </Link>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  )
}
