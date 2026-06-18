import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, test } from 'vitest';
import App from '../App';
import { installMockApi } from '../test/mockApi';
import { renderWithClient } from '../test/render';

describe('Reservation page', () => {
  test('loads slots and shows success after reservation submission', async () => {
    installMockApi();
    renderWithClient(<App />);

    fireEvent.click(await screen.findByText('10:00-12:00'));

    expect(await screen.findByText(/预约成功/)).toBeInTheDocument();
    expect(screen.getByText(/RSV20260619-101/)).toBeInTheDocument();
  });

  test('shows backend error when slot is full', async () => {
    installMockApi({ failReservations: true });
    renderWithClient(<App />);

    fireEvent.click(await screen.findByText('10:00-12:00'));

    expect(await screen.findByText(/预约失败/)).toBeInTheDocument();
    expect(screen.getByText(/该时段已满/)).toBeInTheDocument();
  });
});
