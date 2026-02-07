import { cn } from '@/lib/utils';

interface RiskScoreGaugeProps {
  score: number;
  size?: 'sm' | 'md' | 'lg';
  showLabel?: boolean;
}

const RiskScoreGauge = ({ score, size = 'md', showLabel = true }: RiskScoreGaugeProps) => {
  const getRiskLevel = (score: number) => {
    if (score <= 30) return { label: 'Low Risk', color: 'text-risk-low', bgColor: 'bg-risk-low' };
    if (score <= 60) return { label: 'Medium Risk', color: 'text-risk-medium', bgColor: 'bg-risk-medium' };
    return { label: 'High Risk', color: 'text-risk-high', bgColor: 'bg-risk-high' };
  };

  const risk = getRiskLevel(score);
  const rotation = (score / 100) * 180 - 90; // -90 to 90 degrees

  const sizeConfig = {
    sm: { container: 'w-20 h-10', text: 'text-lg', label: 'text-xs' },
    md: { container: 'w-32 h-16', text: 'text-2xl', label: 'text-sm' },
    lg: { container: 'w-48 h-24', text: 'text-4xl', label: 'text-base' },
  };

  const config = sizeConfig[size];

  return (
    <div className="flex flex-col items-center gap-2">
      <div className={cn('relative', config.container)}>
        {/* Background arc */}
        <div className="absolute inset-0 overflow-hidden">
          <div
            className="w-full h-[200%] rounded-full"
            style={{
              background: `conic-gradient(
                hsl(var(--risk-low)) 0deg 60deg,
                hsl(var(--risk-medium)) 60deg 120deg,
                hsl(var(--risk-high)) 120deg 180deg,
                transparent 180deg 360deg
              )`,
            }}
          />
        </div>
        
        {/* Inner circle (mask) */}
        <div className="absolute inset-[20%] bg-card rounded-full" />
        
        {/* Needle */}
        <div
          className="absolute bottom-0 left-1/2 origin-bottom h-[45%] w-0.5 bg-foreground rounded-full transition-transform duration-700 ease-out"
          style={{ transform: `translateX(-50%) rotate(${rotation}deg)` }}
        />
        
        {/* Center dot */}
        <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-2 h-2 bg-foreground rounded-full" />
        
        {/* Score */}
        <div className="absolute inset-0 flex items-end justify-center pb-1">
          <span className={cn('font-bold', config.text, risk.color)}>{score}</span>
        </div>
      </div>
      
      {showLabel && (
        <span className={cn('font-medium', config.label, risk.color)}>
          {risk.label}
        </span>
      )}
    </div>
  );
};

export default RiskScoreGauge;
