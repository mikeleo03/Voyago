import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment.prod';
import { Tour } from '../../models/tour.model';

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
    sortPrice?: string
  ): Observable<Tour[]> {
    let params = new HttpParams();
    if (title) params = params.append('title', title);
    if (minPrice) params = params.append('minPrice', minPrice.toString());
    if (maxPrice) params = params.append('maxPrice', maxPrice.toString());
    if (location) params = params.append('location', location);
    if (sortPrice) params = params.append('sortPrice', sortPrice);

    return this.http.get<Tour[]>(this.apiUrl, { params });
  }

  getTourById(id: string): Observable<Tour> {
    return this.http.get<Tour>(`${this.apiUrl}/${id}`);
  }

  createTour(tour: Tour): Observable<Tour> {
    return this.http.post<Tour>(this.apiUrl, tour);
  }

  updateTour(id: string, tour: Tour): Observable<Tour> {
    return this.http.put<Tour>(`${this.apiUrl}/${id}`, tour);
  }

  importToursFromCsv(file: File): Observable<string> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.http.post<string>(`${this.apiUrl}/import`, formData);
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
