import { useParams, useNavigate } from 'react-router-dom';
import DashboardLayout from '@/components/layout/DashboardLayout';
import { mockApplications, calculateRiskFactors } from '@/data/mockData';
import { useAuth } from '@/contexts/AuthContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { Textarea } from '@/components/ui/textarea';
import StatusBadge from '@/components/common/StatusBadge';
import RiskScoreGauge from '@/components/common/RiskScoreGauge';
import RiskFactorCard from '@/components/common/RiskFactorCard';
import {
  ArrowLeft,
  User,
  Mail,
  Phone,
  Briefcase,
  Calendar,
  DollarSign,
  FileText,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Sparkles,
  Download,
  Eye,
} from 'lucide-react';
import { format } from 'date-fns';

const ApplicationDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const application = mockApplications.find((app) => app.id === id);

  if (!application) {
    return (
      <DashboardLayout title="Application Not Found">
        <div className="flex flex-col items-center justify-center h-96">
          <FileText className="w-16 h-16 text-muted-foreground mb-4" />
          <h2 className="text-xl font-semibold mb-2">Application Not Found</h2>
          <p className="text-muted-foreground mb-4">
            The application you're looking for doesn't exist.
          </p>
          <Button onClick={() => navigate('/applications')}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Applications
          </Button>
        </div>
      </DashboardLayout>
    );
  }

  const riskFactors = calculateRiskFactors(application);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
    }).format(amount);
  };

  const getLoanTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      personal: 'Personal Loan',
      home: 'Home Loan',
      auto: 'Auto Loan',
      business: 'Business Loan',
      education: 'Education Loan',
    };
    return labels[type] || type;
  };

  const isOfficerOrAdmin = user?.role === 'officer' || user?.role === 'admin';

  return (
    <DashboardLayout>
      <div className="animate-fade-in">
        {/* Header */}
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 mb-6">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div>
              <div className="flex items-center gap-3">
                <h1 className="text-2xl font-bold text-foreground">{application.id}</h1>
                <StatusBadge status={application.status} />
              </div>
              <p className="text-muted-foreground">
                Submitted on {format(new Date(application.submittedAt), 'MMMM d, yyyy')}
              </p>
            </div>
          </div>

          {isOfficerOrAdmin && application.status === 'pending' || application.status === 'manual_review' ? (
            <div className="flex gap-3">
              <Button variant="outline" className="border-status-rejected text-status-rejected hover:bg-status-rejected/10">
                <XCircle className="w-4 h-4 mr-2" />
                Reject
              </Button>
              <Button variant="outline" className="border-status-review text-status-review hover:bg-status-review/10">
                <AlertTriangle className="w-4 h-4 mr-2" />
                Request Review
              </Button>
              <Button className="bg-status-approved hover:bg-status-approved/90">
                <CheckCircle className="w-4 h-4 mr-2" />
                Approve
              </Button>
            </div>
          ) : null}
        </div>

        <Tabs defaultValue="overview" className="space-y-6">
          <TabsList>
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="risk-analysis">Risk Analysis</TabsTrigger>
            <TabsTrigger value="documents">Documents</TabsTrigger>
            <TabsTrigger value="ai-insights">AI Insights</TabsTrigger>
          </TabsList>

          {/* Overview Tab */}
          <TabsContent value="overview" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              {/* Applicant Info */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Applicant Information</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <User className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Full Name</p>
                      <p className="font-medium">{application.applicantName}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <Mail className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Email</p>
                      <p className="font-medium">{application.email}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <Phone className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Phone</p>
                      <p className="font-medium">{application.phone}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <Briefcase className="w-5 h-5 text-primary" />
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Employment</p>
                      <p className="font-medium capitalize">{application.employmentType}</p>
                      <p className="text-sm text-muted-foreground">{application.employmentDuration} years</p>
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Loan Details */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Loan Details</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <p className="text-sm text-muted-foreground">Loan Type</p>
                    <p className="text-lg font-semibold">{getLoanTypeLabel(application.loanType)}</p>
                  </div>
                  <Separator />
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground">Amount</p>
                      <p className="text-xl font-bold text-primary">
                        {formatCurrency(application.loanAmount)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Term</p>
                      <p className="text-lg font-semibold">{application.loanTerm} months</p>
                    </div>
                  </div>
                  <Separator />
                  <div>
                    <p className="text-sm text-muted-foreground">Purpose</p>
                    <p className="text-sm mt-1">{application.purpose}</p>
                  </div>
                </CardContent>
              </Card>

              {/* Risk Score */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Risk Assessment</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col items-center">
                  <RiskScoreGauge score={application.riskScore} size="lg" />
                  <div className="grid grid-cols-2 gap-4 w-full mt-6">
                    <div className="text-center p-3 bg-secondary/50 rounded-lg">
                      <p className="text-sm text-muted-foreground">DTI Ratio</p>
                      <p className="text-lg font-semibold">{application.dti}%</p>
                    </div>
                    <div className="text-center p-3 bg-secondary/50 rounded-lg">
                      <p className="text-sm text-muted-foreground">Credit Score</p>
                      <p className="text-lg font-semibold">{application.creditScore}</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Financial Summary */}
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Financial Summary</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                  <div>
                    <p className="text-sm text-muted-foreground">Annual Income</p>
                    <p className="text-xl font-bold">{formatCurrency(application.annualIncome)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Monthly Expenses</p>
                    <p className="text-xl font-bold">{formatCurrency(application.monthlyExpenses)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Existing Debts</p>
                    <p className="text-xl font-bold">{formatCurrency(application.existingDebts)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Loan-to-Income</p>
                    <p className="text-xl font-bold">{application.lti}x</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Risk Analysis Tab */}
          <TabsContent value="risk-analysis" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              <Card className="lg:col-span-2">
                <CardHeader>
                  <CardTitle>Risk Factor Breakdown</CardTitle>
                  <CardDescription>
                    Detailed analysis of each risk component
                  </CardDescription>
                </CardHeader>
                <CardContent className="grid gap-4 md:grid-cols-2">
                  {riskFactors.map((factor) => (
                    <RiskFactorCard key={factor.name} factor={factor} />
                  ))}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Overall Risk Score</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col items-center py-8">
                  <RiskScoreGauge score={application.riskScore} size="lg" />
                  <div className="mt-6 w-full space-y-2">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Score Threshold</span>
                      <span className="font-medium">
                        {application.riskScore <= 30
                          ? 'Auto-Approve'
                          : application.riskScore <= 60
                          ? 'Manual Review'
                          : 'Auto-Reject'}
                      </span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Confidence</span>
                      <span className="font-medium">92%</span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Documents Tab */}
          <TabsContent value="documents" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Uploaded Documents</CardTitle>
                <CardDescription>
                  {application.documents.length} document(s) uploaded
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {application.documents.map((doc) => (
                    <div
                      key={doc.id}
                      className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg border border-border"
                    >
                      <div className="flex items-center gap-4">
                        <div className="p-2 bg-primary/10 rounded-lg">
                          <FileText className="w-5 h-5 text-primary" />
                        </div>
                        <div>
                          <p className="font-medium">{doc.name}</p>
                          <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <span className="capitalize">{doc.type.replace('_', ' ')}</span>
                            <span>•</span>
                            <span>Uploaded {format(new Date(doc.uploadedAt), 'MMM d, yyyy')}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        {doc.verified ? (
                          <span className="flex items-center gap-1 text-sm text-status-approved">
                            <CheckCircle className="w-4 h-4" />
                            Verified
                          </span>
                        ) : (
                          <span className="flex items-center gap-1 text-sm text-status-review">
                            <AlertTriangle className="w-4 h-4" />
                            Pending
                          </span>
                        )}
                        <Button variant="ghost" size="icon">
                          <Eye className="w-4 h-4" />
                        </Button>
                        <Button variant="ghost" size="icon">
                          <Download className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>

                {application.documents.some((d) => d.extractedData) && (
                  <div className="mt-6">
                    <h4 className="font-medium mb-3">Extracted Data</h4>
                    <div className="p-4 bg-accent/10 rounded-lg border border-accent/20">
                      {application.documents
                        .filter((d) => d.extractedData)
                        .map((doc) => (
                          <div key={doc.id}>
                            <p className="text-sm text-muted-foreground mb-2">From: {doc.name}</p>
                            <pre className="text-sm bg-secondary/50 p-3 rounded">
                              {JSON.stringify(doc.extractedData, null, 2)}
                            </pre>
                          </div>
                        ))}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* AI Insights Tab */}
          <TabsContent value="ai-insights" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-2">
              <Card>
                <CardHeader>
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-5 h-5 text-primary" />
                    <CardTitle>AI Decision Explanation</CardTitle>
                  </div>
                  <CardDescription>
                    Gemini-powered analysis of this application
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="p-4 bg-secondary/50 rounded-lg border border-border">
                    <p className="text-foreground leading-relaxed">
                      {application.aiExplanation || 'AI analysis pending...'}
                    </p>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-5 h-5 text-accent" />
                    <CardTitle>Improvement Suggestions</CardTitle>
                  </div>
                  <CardDescription>
                    AI-generated recommendations for the applicant
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {application.aiSuggestions && application.aiSuggestions.length > 0 ? (
                    <ul className="space-y-3">
                      {application.aiSuggestions.map((suggestion, index) => (
                        <li
                          key={index}
                          className="flex items-start gap-3 p-3 bg-accent/10 rounded-lg border border-accent/20"
                        >
                          <span className="flex-shrink-0 w-6 h-6 bg-accent text-accent-foreground rounded-full flex items-center justify-center text-sm font-medium">
                            {index + 1}
                          </span>
                          <p className="text-sm">{suggestion}</p>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-muted-foreground">No suggestions available.</p>
                  )}
                </CardContent>
              </Card>
            </div>

            {isOfficerOrAdmin && (
              <Card>
                <CardHeader>
                  <CardTitle>Officer Notes</CardTitle>
                  <CardDescription>Add notes for this application</CardDescription>
                </CardHeader>
                <CardContent>
                  <Textarea
                    placeholder="Enter your notes here..."
                    className="min-h-[100px]"
                  />
                  <Button className="mt-3">Save Notes</Button>
                </CardContent>
              </Card>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </DashboardLayout>
  );
};

export default ApplicationDetail;
