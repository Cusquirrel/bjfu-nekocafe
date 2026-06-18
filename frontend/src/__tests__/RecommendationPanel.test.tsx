import { screen } from '@testing-library/react';
import { describe, expect, test } from 'vitest';
import App from '../App';
import { installMockApi } from '../test/mockApi';
import { renderWithClient } from '../test/render';

describe('Recommendation panel', () => {
  test('does not confuse orange cat recommendation with orange menu package', async () => {
    installMockApi({ recommendationCats: [{ id: 9, name: '橘猫豆豆', breed: '中华田园猫', interaction_status: 'AVAILABLE' }] });
    renderWithClient(<App />);

    expect(await screen.findByText('橘猫豆豆')).toBeInTheDocument();
    expect(screen.queryByText('橘子套餐')).not.toBeInTheDocument();
  });
});
