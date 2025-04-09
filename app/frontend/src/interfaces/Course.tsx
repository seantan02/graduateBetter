export interface Course {
  id: string;
  code: string;
  title: string;
  minCredits: number;
  maxCredits: number;
  requisiteString: string;
  courseGroupId: number;
}

export interface CourseSearchProps {
  selectedCourses: Course[];
  setSelectedCourses: React.Dispatch<React.SetStateAction<Course[]>>;
  title: string;
}

export interface CourseResult {
    id: string;
    code: string;
    title: string;
    credits: number;
}