export interface Major {
  id: string;
  code: string;
  title: string;
  majorRequirements: Array<number>;
}

export interface MajorSearchProps {
  selectedMajors: Major[];
  setSelectedMajors: React.Dispatch<React.SetStateAction<Major[]>>;
}