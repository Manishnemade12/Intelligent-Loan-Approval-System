import { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, FileText, X, CheckCircle, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface UploadedFile {
  id: string;
  file: File;
  status: 'uploading' | 'success' | 'error';
  progress: number;
}

interface DocumentUploadProps {
  onFilesChange: (files: File[]) => void;
  maxFiles?: number;
  accept?: Record<string, string[]>;
}

const DocumentUpload = ({ onFilesChange, maxFiles = 5, accept }: DocumentUploadProps) => {
  const [uploadedFiles, setUploadedFiles] = useState<UploadedFile[]>([]);

  const onDrop = useCallback((acceptedFiles: File[]) => {
    const newFiles = acceptedFiles.map((file) => ({
      id: Math.random().toString(36).substr(2, 9),
      file,
      status: 'uploading' as const,
      progress: 0,
    }));

    setUploadedFiles((prev) => [...prev, ...newFiles]);

    // Simulate upload progress
    newFiles.forEach((uploadedFile) => {
      let progress = 0;
      const interval = setInterval(() => {
        progress += 10;
        setUploadedFiles((prev) =>
          prev.map((f) =>
            f.id === uploadedFile.id
              ? { ...f, progress, status: progress >= 100 ? 'success' : 'uploading' }
              : f
          )
        );
        if (progress >= 100) {
          clearInterval(interval);
        }
      }, 200);
    });

      onFilesChange(selectedFiles);
      if (selectedFiles.length > 0) {
        setUploading(true);
        setError(null);
        try {
          const formData = new FormData();
          selectedFiles.forEach(file => formData.append('file', file));
          // TODO: Add applicationId and documentType as needed
          await import('../../lib/api').then(({ api }) => api.uploadDocument(formData));
        } catch (err) {
          setError('Failed to upload document');
        } finally {
          setUploading(false);
        }
      }
  }, [uploadedFiles, onFilesChange]);

  const removeFile = (id: string) => {
    setUploadedFiles((prev) => prev.filter((f) => f.id !== id));
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    maxFiles,
    accept: accept || {
      'application/pdf': ['.pdf'],
      'image/*': ['.png', '.jpg', '.jpeg'],
    },
  });

  return (
    <div className="space-y-4">
      <div
        {...getRootProps()}
        className={cn(
          'border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-all',
          isDragActive
            ? 'border-primary bg-primary/5'
            : 'border-border hover:border-primary/50 hover:bg-secondary/50'
        )}
      >
        <input {...getInputProps()} />
        <Upload className="w-10 h-10 mx-auto mb-4 text-muted-foreground" />
        <p className="text-foreground font-medium">
          {isDragActive ? 'Drop files here' : 'Drag & drop files here'}
        </p>
        <p className="text-sm text-muted-foreground mt-1">or click to browse</p>
        <p className="text-xs text-muted-foreground mt-2">
          Supports PDF, PNG, JPG (max {maxFiles} files)
        </p>
      </div>

      {/* Uploaded Files List */}
      {uploadedFiles.length > 0 && (
        <div className="space-y-2">
          {uploadedFiles.map((uploadedFile) => (
            <div
              key={uploadedFile.id}
              className="flex items-center gap-3 p-3 bg-secondary/50 rounded-lg border border-border"
            >
              <FileText className="w-5 h-5 text-primary" />
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">{uploadedFile.file.name}</p>
                <div className="flex items-center gap-2">
                  {uploadedFile.status === 'uploading' ? (
                    <>
                      <div className="flex-1 h-1 bg-secondary rounded-full overflow-hidden">
                        <div
                          className="h-full bg-primary transition-all duration-200"
                          style={{ width: `${uploadedFile.progress}%` }}
                        />
                      </div>
                      <span className="text-xs text-muted-foreground">{uploadedFile.progress}%</span>
                    </>
                  ) : (
                    <span className="text-xs text-status-approved flex items-center gap-1">
                      <CheckCircle className="w-3 h-3" />
                      Uploaded
                    </span>
                  )}
                </div>
              </div>
              {uploadedFile.status === 'uploading' ? (
                <Loader2 className="w-4 h-4 animate-spin text-muted-foreground" />
              ) : (
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8"
                  onClick={() => removeFile(uploadedFile.id)}
                >
                  <X className="w-4 h-4" />
                </Button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DocumentUpload;
