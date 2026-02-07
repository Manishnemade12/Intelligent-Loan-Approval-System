import { RiskFactor } from '@/types/loan';
import { cn } from '@/lib/utils';
import { Progress } from '@/components/ui/progress';
import { CheckCircle, AlertTriangle, XCircle } from 'lucide-react';

interface RiskFactorCardProps {
  factor: RiskFactor;
}

const RiskFactorCard = ({ factor }: RiskFactorCardProps) => {
  const statusConfig = {
    good: {
      icon: CheckCircle,
      color: 'text-status-approved',
      progressColor: 'bg-status-approved',
    },
    warning: {
      icon: AlertTriangle,
      color: 'text-status-review',
      progressColor: 'bg-status-review',
    },
    critical: {
      icon: XCircle,
      color: 'text-status-rejected',
      progressColor: 'bg-status-rejected',
    },
  };

  const { icon: StatusIcon, color, progressColor } = statusConfig[factor.status];

  return (
    <div className="p-4 rounded-lg bg-secondary/50 border border-border">
      <div className="flex items-start justify-between mb-2">
        <div>
          <h4 className="font-medium text-foreground">{factor.name}</h4>
          <p className="text-sm text-muted-foreground">{factor.description}</p>
        </div>
        <StatusIcon className={cn('w-5 h-5', color)} />
      </div>
      <div className="mt-3">
        <div className="flex justify-between text-xs text-muted-foreground mb-1">
          <span>Score: {Math.round(factor.score)}/100</span>
          <span>Weight: {factor.weight}%</span>
        </div>
        <div className="h-2 bg-secondary rounded-full overflow-hidden">
          <div
            className={cn('h-full rounded-full transition-all duration-500', progressColor)}
            style={{ width: `${factor.score}%` }}
          />
        </div>
      </div>
    </div>
  );
};

export default RiskFactorCard;
