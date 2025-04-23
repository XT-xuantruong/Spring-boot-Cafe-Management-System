import { Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { User } from '@/interfaces/user';
import { RootState } from '@/stores';
import { useLogoutMutation } from '@/services/AuthServices';

export default function Header() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user as User | null);
  const [logout] = useLogoutMutation();

  const handleLogout = () => {
    logout().unwrap();
    navigate('/login');
  };

  const MenuItem = ({ to, label, onClick }: { to: string; label: string; onClick?: () => void }) => (
    <DropdownMenuItem asChild>
      <Link to={to} className="w-full text-[#1E3A8A] hover:bg-[#2563EB] hover:text-white" onClick={onClick}>
        {label}
      </Link>
    </DropdownMenuItem>
  );

  return (
    <header className="fixed inset-x-0 top-0 z-50 bg-[#FFFFFF] text-[#1E3A8A] shadow-md">
      <div className="mx-auto flex items-center justify-between px-5 py-1">
        {/* Logo */}
        <Link to="/" className="text-2xl font-bold text-[#1E3A8A]">
          Coffee Shop
        </Link>

        {/* Right Section */}
        <div className="flex items-center space-x-3">
          {user ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-14 w-14 rounded-full hover:bg-[#60A5FA] hover:text-white transition-colors"
                  aria-label="Profile"
                >
                  <Avatar className="h-10 w-10 border-2 border-[#60A5FA] shadow-[0_0_8px_#60A5FA] hover:scale-110 transition-transform">
                    <AvatarImage src={user?.avatarUrl || ""} alt={user?.name} />
                    <AvatarFallback className="bg-[#60A5FA] text-white">
                      {user?.name?.charAt(0) || 'U'}
                    </AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-56 z-[60] bg-white" align="end">
                <DropdownMenuLabel className="text-[#1E3A8A]">My Account</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <MenuItem to={`/profile`} label="Profile" />
                <DropdownMenuSeparator />
                <MenuItem to="/login" label="Logout" onClick={handleLogout} />
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <>
              <Button
                variant="outline"
                className="border-[#60A5FA] text-[#1E3A8A] hover:bg-[#2563EB] hover:text-white transition-colors"
                onClick={() => navigate('/login')}
              >
                Đăng nhập
              </Button>
              <Button
                variant="outline"
                className="border-[#60A5FA] text-[#1E3A8A] hover:bg-[#2563EB] hover:text-white transition-colors"
                onClick={() => navigate('/register')}
              >
                Đăng ký
              </Button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}