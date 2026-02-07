import { Bell, Search, Settings } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/contexts/AuthContext';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { UserRole } from '@/types/loan';

interface HeaderProps {
  title?: string;
}

const Header = ({ title }: HeaderProps) => {
  const { user, switchRole } = useAuth();

  return (
    <header className="h-16 border-b border-border bg-card/50 backdrop-blur-sm flex items-center justify-between px-6">
      <div className="flex items-center gap-4">
        {title && <h1 className="text-xl font-semibold text-foreground">{title}</h1>}
      </div>

      <div className="flex items-center gap-4">
        {/* Search */}
        <div className="relative hidden md:block">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search applications..."
            className="w-64 pl-10 bg-secondary/50 border-border focus:bg-card"
          />
        </div>

        {/* Role Switcher (Demo) */}
        <div className="flex items-center gap-2">
          <span className="text-xs text-muted-foreground hidden sm:block">Demo View:</span>
          <Select value={user?.role} onValueChange={(value: UserRole) => switchRole(value)}>
            <SelectTrigger className="w-32 h-9 text-sm">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="customer">Customer</SelectItem>
              <SelectItem value="officer">Officer</SelectItem>
              <SelectItem value="admin">Admin</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Notifications */}
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1 right-1 w-2 h-2 bg-destructive rounded-full"></span>
        </Button>

        {/* Settings */}
        <Button variant="ghost" size="icon">
          <Settings className="w-5 h-5" />
        </Button>
      </div>
    </header>
  );
};

export default Header;
