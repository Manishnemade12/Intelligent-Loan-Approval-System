import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon?: LucideIcon;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'danger';
}

const StatCard = ({ title, value, subtitle, icon: Icon, trend, variant = 'default' }: StatCardProps) => {
  const variantStyles = {
    default: 'bg-card',
    primary: 'bg-primary/5 border-primary/20',
    success: 'bg-status-approved-bg border-status-approved/20',
    warning: 'bg-status-review-bg border-status-review/20',
    danger: 'bg-status-rejected-bg border-status-rejected/20',
  };

  const iconStyles = {
    default: 'bg-secondary text-foreground',
    primary: 'bg-primary/10 text-primary',
    success: 'bg-status-approved/10 text-status-approved',
    warning: 'bg-status-review/10 text-status-review',
    danger: 'bg-status-rejected/10 text-status-rejected',
  };

  return (
    <Card className={cn('border transition-shadow hover:shadow-card-hover', variantStyles[variant])}>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
        {Icon && (
          <div className={cn('p-2 rounded-lg', iconStyles[variant])}>
            <Icon className="w-4 h-4" />
          </div>
        )}
      </CardHeader>
      <CardContent>
        <div className="flex items-baseline gap-2">
          <span className="text-2xl font-bold text-foreground">{value}</span>
          {trend && (
            <span
              className={cn(
                'text-xs font-medium',
                trend.isPositive ? 'text-status-approved' : 'text-status-rejected'
              )}
            >
              {trend.isPositive ? '+' : ''}{trend.value}%
            </span>
          )}
        </div>
        {subtitle && <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>}
      </CardContent>
    </Card>
  );
};

export default StatCard;
