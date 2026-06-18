import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, test } from 'vitest';
import App from '../App';
import { installMockApi } from '../test/mockApi';
import { renderWithClient } from '../test/render';

describe('Dashboard page', () => {
  test('renders operation metrics from dashboard api', async () => {
    installMockApi({ dashboard: { reservations: 12, completed: 9, cancelled: 2, watchCats: 1, revenueCents: 26500 } });
    renderWithClient(<App />);

    fireEvent.click(await screen.findByText('运营看板'));

    expect(await screen.findByText('总预约数')).toBeInTheDocument();
    expect(screen.getByText('12')).toBeInTheDocument();
    expect(screen.getByText('¥265.00')).toBeInTheDocument();
  });
});
