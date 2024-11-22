export interface Facility {
    id: string;
    tourId: string;
    name: string;
}  

export interface FacilityDTO {
    name: string;
    tourId: string;
    id?: string;
}