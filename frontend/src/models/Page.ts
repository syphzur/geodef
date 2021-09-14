/**
 * Equivalent of Spring Boot Page collection
 */
export class Page<T> {
  content: Array<T>;
  // if page is last
  last?: boolean;
  // total elements (on all pages)
  totalElements?: number;
  // number of pages
  totalPages?: number;
  // page number
  number?: number;
  // number of elements on page
  numberOfElements?: number;

  constructor(content?: Array<T>) {
    this.content = content ?? [];
  }
}
