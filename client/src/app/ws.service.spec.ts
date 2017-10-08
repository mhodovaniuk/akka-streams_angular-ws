import { TestBed, inject } from '@angular/core/testing';

import { WSService } from './ws.service';

describe('WSService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WSService]
    });
  });

  it('should ...', inject([WSService], (service: WSService) => {
    expect(service).toBeTruthy();
  }));
});
