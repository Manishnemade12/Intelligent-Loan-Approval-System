import { Link, useLocation } from 'react-router-dom';
import { cn } from '@/lib/utils';
import { useAuth } from '@/contexts/AuthContext';
import {
  LayoutDashboard,
  FileText,
  PlusCircle,
  Users,
  Settings,
  LogOut,
  Shield,
  ClipboardList,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';
import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';

const Sidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  const customerNav = [
    { name: 'Dashboard', href: '/', icon: LayoutDashboard },
    { name: 'My Applications', href: '/applications', icon: FileText },
    { name: 'Apply for Loan', href: '/apply', icon: PlusCircle },
  ];

  const officerNav = [
    { name: 'Dashboard', href: '/', icon: LayoutDashboard },
    { name: 'Applications', href: '/applications', icon: ClipboardList },
    { name: 'My Queue', href: '/queue', icon: FileText },
  ];

  const adminNav = [
    { name: 'Dashboard', href: '/', icon: LayoutDashboard },
    { name: 'All Applications', href: '/applications', icon: ClipboardList },
    { name: 'User Management', href: '/users', icon: Users },
    { name: 'Settings', href: '/settings', icon: Settings },
  ];

  const getNavItems = () => {
    switch (user?.role) {
      case 'customer':
        return customerNav;
      case 'officer':
        return officerNav;
      case 'admin':
        return adminNav;
      default:
        return customerNav;
    }
  };

  const navItems = getNavItems();

  const getRoleBadge = () => {
    switch (user?.role) {
      case 'admin':
        return { label: 'Admin', color: 'bg-primary/20 text-primary-foreground' };
      case 'officer':
        return { label: 'Loan Officer', color: 'bg-accent/20 text-accent' };
      default:
        return { label: 'Customer', color: 'bg-sidebar-accent text-sidebar-foreground' };
    }
  };

  const roleBadge = getRoleBadge();

  return (
    <aside
      className={cn(
        'flex flex-col h-screen bg-sidebar text-sidebar-foreground border-r border-sidebar-border transition-all duration-300',
        collapsed ? 'w-20' : 'w-64'
      )}
    >
      {/* Logo */}
      <div className="flex items-center justify-between h-16 px-4 border-b border-sidebar-border">
        {!collapsed && (
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center">
              <Shield className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="font-semibold text-lg text-sidebar-primary-foreground">LoanFlow</span>
          </div>
        )}
        {collapsed && (
          <div className="w-10 h-10 rounded-lg bg-primary flex items-center justify-center mx-auto">
            <Shield className="w-6 h-6 text-primary-foreground" />
          </div>
        )}
        <Button
          variant="ghost"
          size="icon"
          onClick={() => setCollapsed(!collapsed)}
          className="text-sidebar-muted hover:text-sidebar-foreground hover:bg-sidebar-accent"
        >
          {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
        </Button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-1">
        {navItems.map((item) => {
          const isActive = location.pathname === item.href;
          const Icon = item.icon;

          const navLink = (
            <Link
              key={item.name}
              to={item.href}
              className={cn(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all',
                isActive
                  ? 'bg-sidebar-primary text-sidebar-primary-foreground'
                  : 'text-sidebar-muted hover:text-sidebar-foreground hover:bg-sidebar-accent'
              )}
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              {!collapsed && <span>{item.name}</span>}
            </Link>
          );

          if (collapsed) {
            return (
              <Tooltip key={item.name} delayDuration={0}>
                <TooltipTrigger asChild>{navLink}</TooltipTrigger>
                <TooltipContent side="right" className="bg-foreground text-background">
                  {item.name}
                </TooltipContent>
              </Tooltip>
            );
          }

          return navLink;
        })}
      </nav>

      {/* User Section */}
      <div className="p-4 border-t border-sidebar-border">
        <div className={cn('flex items-center gap-3', collapsed && 'justify-center')}>
          <Avatar className="h-10 w-10">
            <AvatarFallback className="bg-sidebar-accent text-sidebar-foreground">
              {user?.name?.charAt(0) || 'U'}
            </AvatarFallback>
          </Avatar>
          {!collapsed && (
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-sidebar-primary-foreground truncate">
                {user?.name}
              </p>
              <span className={cn('text-xs px-2 py-0.5 rounded-full', roleBadge.color)}>
                {roleBadge.label}
              </span>
            </div>
          )}
        </div>
        {!collapsed && (
          <Button
            variant="ghost"
            size="sm"
            onClick={logout}
            className="w-full mt-3 text-sidebar-muted hover:text-sidebar-foreground hover:bg-sidebar-accent"
          >
            <LogOut className="w-4 h-4 mr-2" />
            Sign out
          </Button>
        )}
      </div>
    </aside>
  );
};

export default Sidebar;
