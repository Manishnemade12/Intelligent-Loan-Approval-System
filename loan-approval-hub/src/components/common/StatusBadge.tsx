import { LoanStatus } from '@/types/loan';
import { cn } from '@/lib/utils';
import { CheckCircle, XCircle, Clock, AlertTriangle } from 'lucide-react';

interface StatusBadgeProps {
  status: LoanStatus;
  size?: 'sm' | 'md' | 'lg';
}

const StatusBadge = ({ status, size = 'md' }: StatusBadgeProps) => {
  const config = {
    approved: {
      label: 'Approved',
      icon: CheckCircle,
      className: 'status-badge-approved',
    },
    rejected: {
      label: 'Rejected',
      icon: XCircle,
      className: 'status-badge-rejected',
    },
    pending: {
      label: 'Pending',
      icon: Clock,
      className: 'status-badge-pending',
    },
    manual_review: {
      label: 'Manual Review',
      icon: AlertTriangle,
      className: 'status-badge-review',
    },
  };

  const { label, icon: Icon, className } = config[status];

  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-sm px-2.5 py-1',
    lg: 'text-base px-3 py-1.5',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 font-medium rounded-full border',
        className,
        sizeClasses[size]
      )}
    >
      <Icon className={cn(
        size === 'sm' ? 'w-3 h-3' : size === 'md' ? 'w-4 h-4' : 'w-5 h-5'
      )} />
      {label}
    </span>
  );
};

export default StatusBadge;
