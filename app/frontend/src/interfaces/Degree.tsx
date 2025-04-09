import { CourseResult } from "./Course.tsx";

export interface GraduationPath {
    courses: CourseResult[];
    totalCredits: number;
}

export interface ResultDisplayProps {
    graduationPath: GraduationPath;
}