import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment.prod';
import { Tour, TourSave } from '../../models/tour.model';
@Injectable({
  providedIn: 'root'
})
export class TourService {
  private apiUrl = `${environment.apiUrl}/tour`;

  constructor(private http: HttpClient) { }

  getTours(
    title?: string,
    minPrice?: number,
    maxPrice?: number,
    location?: string,
    sortPrice?: string,
    page: number = 1,
    limit: number = 10
  ): Observable<{ tours: Tour[], currentPage: number, totalItems: number, totalPages: number }> {
      let params = new HttpParams();
      if (title) params = params.append('title', title);
      if (minPrice !== undefined) params = params.append('minPrice', minPrice.toString());
      if (maxPrice !== undefined) params = params.append('maxPrice', maxPrice.toString());
      if (location) params = params.append('location', location);
      if (sortPrice) params = params.append('sortPrice', sortPrice);
      params = params.append('page', (page - 1).toString());
      params = params.append('size', limit.toString());

      return this.http.get<{ tours: Tour[], currentPage: number, totalItems: number, totalPages: number }>(this.apiUrl, { params });
  } 

  getTourById(id: string): Observable<Tour> {
    return this.http.get<Tour>(`${this.apiUrl}/${id}`);
  }

  createTour(tour: TourSave, imageFile?: File): Observable<Tour> {
    const formData: FormData = new FormData();

    formData.append('tour', new Blob([JSON.stringify(tour)], { type: 'application/json' }));

    if (imageFile) {
      formData.append('file', imageFile);
    }

    return this.http.post<Tour>(this.apiUrl, formData);
  }

  getTourImage(tourId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${tourId}/image`, { responseType: 'blob' });
  }

  updateTour(id: string, tour: TourSave, imageFile?: File): Observable<Tour> {
    const formData: FormData = new FormData();

    formData.append('tour', new Blob([JSON.stringify(tour)], { type: 'application/json' }));

    if (imageFile) {
        formData.append('file', imageFile);
    }

    return this.http.put<Tour>(`${this.apiUrl}/${id}`, formData);
  }


  importToursFromCsv(file: File): Observable<string> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.http.post<string>(`${this.apiUrl}/import`, formData, { responseType: 'text' as 'json' });
  }

  reduceQuota(id: string, quantity: number): Observable<void> {
    const params = new HttpParams()
      .set('id', id)
      .set('quantity', quantity.toString());

    return this.http.put<void>(`${this.apiUrl}/reduce`, null, { params });
  }

  updateTourStatus(id: string): Observable<void> {
    const params = new HttpParams().set('id', id);
    return this.http.put<void>(`${this.apiUrl}/status`, null, { params });
  }
}
