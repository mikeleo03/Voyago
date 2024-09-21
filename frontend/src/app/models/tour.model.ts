import { Facility } from "./facility.model";
import { Review } from "./review.model";

export interface Tour {
    id: string;
    title: string;
    detail: string;
    quota: number;
    prices: number;
    location: string;
    image?: string;
    status: string;
    facilities?: Facility[];
    reviews?: Review[];
  }