package com.music.window;

import com.music.dao.SongDao;
import com.music.pojo.Song;
import com.music.pojo.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class SongEdit extends JFrame {
    private List<Song> songs;
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel editPanel;
    private JPanel tablePanel;
    private User currentUser;
    
    private JTextField searchTitleField;
    private JComboBox<String> genreComboBox;
    private JComboBox<String> artistComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    
    private JTextField titleField;
    private JTextField genreField;
    private JTextField artistIdField;
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private JButton clearEditButton;
    
    private JTable songTable;
    private JScrollPane tableScrollPane;

    public SongEdit(User user) {
        super("歌曲管理");
        this.currentUser = user;
        initComponents();
        initTable();
        setupPermissions();
    }

    private void setupPermissions() {
        if (currentUser != null) {
            String permissions = currentUser.getPermissions();
            if ("view".equals(permissions)) {
                addButton.setEnabled(false);
                modifyButton.setEnabled(false);
                deleteButton.setEnabled(false);
                titleField.setEditable(false);
                genreField.setEditable(false);
                artistIdField.setEditable(false);
            } else if ("edit".equals(permissions)) {
                addButton.setEnabled(true);
                modifyButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        }
    }

    private void initComponents() {
        setPreferredSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        // 创建主面板
        mainPanel = new JPanel(new BorderLayout());
        
        // 创建搜索面板
        searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // 创建编辑面板
        editPanel = createEditPanel();
        mainPanel.add(editPanel, BorderLayout.WEST);
        
        // 创建表格面板
        tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("搜索条件"));
        
        searchTitleField = new JTextField(15);
        genreComboBox = new JComboBox<>(new String[]{"全部", "Pop", "Rock", "Classical", "Jazz", "Hip Hop", "R&B"});
        artistComboBox = new JComboBox<>(new String[]{"全部", "Charlie XCX", "Taylor Swift", "Billie Eilish", "Dua Lipa", "The Weeknd", "Beyoncé", "Kanye West", "Kendrick Lamar"});
        searchButton = new JButton("搜索");
        clearSearchButton = new JButton("清空");
        
        panel.add(new JLabel("歌曲名称:"));
        panel.add(searchTitleField);
        panel.add(new JLabel("流派:"));
        panel.add(genreComboBox);
        panel.add(new JLabel("歌手:"));
        panel.add(artistComboBox);
        panel.add(searchButton);
        panel.add(clearSearchButton);
        
        searchButton.addActionListener(e -> buttonQuery(e));
        clearSearchButton.addActionListener(this::buttonClear);
        
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("歌曲编辑"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        titleField = new JTextField(15);
        genreField = new JTextField(15);
        artistIdField = new JTextField(15);
        
        addButton = new JButton("添加");
        modifyButton = new JButton("修改");
        deleteButton = new JButton("删除");
        clearEditButton = new JButton("清空");
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("歌曲名称:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("流派:"), gbc);
        gbc.gridx = 1;
        panel.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("艺术家ID:"), gbc);
        gbc.gridx = 1;
        panel.add(artistIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(addButton, gbc);
        gbc.gridx = 1;
        panel.add(modifyButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(deleteButton, gbc);
        gbc.gridx = 1;
        panel.add(clearEditButton, gbc);
        
        addButton.addActionListener(this::buttonAdd);
        modifyButton.addActionListener(this::buttonModify);
        deleteButton.addActionListener(this::buttonDelete);
        clearEditButton.addActionListener(this::buttonEditClear);
        
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("歌曲列表"));
        
        String[] columnNames = {"ID", "歌曲名称", "流派", "艺术家ID", "创建时间", "更新时间"};
        songTable = new JTable(new DefaultTableModel(columnNames, 0));
        songTable.getSelectionModel().addListSelectionListener(this::valueChanged);
        
        tableScrollPane = new JScrollPane(songTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void initTable() {
        songs = SongDao.all();
        setTableData(songs);
    }

    private void setTableData(List<Song> songs) {
        DefaultTableModel model = (DefaultTableModel) songTable.getModel();
        model.setRowCount(0);
        
        for (Song song : songs) {
            Object[] row = {
                song.getSongId(),
                song.getTitle(),
                song.getGenre(),
                song.getArtistId(),
                song.getCreatedAt(),
                song.getUpdatedAt()
            };
            model.addRow(row);
        }
    }

    private void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int currentIndex = songTable.getSelectedRow();
            if (currentIndex < 0) return;
            
            Song selectedSong = songs.get(currentIndex);
            titleField.setText(selectedSong.getTitle());
            genreField.setText(selectedSong.getGenre());
            artistIdField.setText(String.valueOf(selectedSong.getArtistId()));
            
            if (currentUser != null) {
                String permissions = currentUser.getPermissions();
                if ("edit".equals(permissions) || "admin".equals(permissions)) {
                    modifyButton.setEnabled(true);
                }
                if ("admin".equals(permissions)) {
                    deleteButton.setEnabled(true);
                }
            }
        }
    }

    private void buttonQuery(ActionEvent e) {
        String title = searchTitleField.getText();
        String genre = (String) genreComboBox.getSelectedItem();
        String artist = (String) artistComboBox.getSelectedItem();
        
        if (!"全部".equals(artist)) {
            // 将歌手名称转换为对应的ID
            int artistId = getArtistId(artist);
            songs = SongDao.queryByArtistId(artistId);
        } else if ("全部".equals(genre)) {
            songs = SongDao.queryByTitle(title);
        } else {
            songs = SongDao.queryByGenre(genre);
        }
        
        setTableData(songs);
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void buttonClear(ActionEvent e) {
        searchTitleField.setText("");
        genreComboBox.setSelectedIndex(0);
        artistComboBox.setSelectedIndex(0);
    }

    private Song getSongFromUI() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入歌曲名称");
            return null;
        }
        if (genreField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入流派");
            return null;
        }
        if (artistIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入艺术家ID");
            return null;
        }

        try {
            Song song = new Song();
            song.setTitle(titleField.getText());
            song.setGenre(genreField.getText());
            song.setArtistId(Integer.parseInt(artistIdField.getText()));
            return song;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "艺术家ID必须是数字");
            return null;
        }
    }

    private void buttonAdd(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(this, 
            "确定将当前信息作为新记录添加到数据库吗？", 
            "确定要添加新记录吗？", 
            JOptionPane.YES_NO_OPTION);
            
        if (n == JOptionPane.YES_OPTION) {
            Song song = getSongFromUI();
            if (song == null) return;
            
            int result = SongDao.insert(song);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "添加成功");
                buttonQuery(null);
            } else {
                JOptionPane.showMessageDialog(this, "添加失败");
            }
        }
    }

    private void buttonModify(ActionEvent e) {
        int currentIndex = songTable.getSelectedRow();
        if (currentIndex == -1) {
            JOptionPane.showMessageDialog(this, "请在表格中选中要修改的记录");
            return;
        }

        int n = JOptionPane.showConfirmDialog(this,
            "确定将当前信息作为修改后记录更新到数据库吗？",
            "确定要修改记录吗？",
            JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            Song song = getSongFromUI();
            if (song == null) return;
            
            song.setSongId(songs.get(currentIndex).getSongId());
            int result = SongDao.update(song);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "修改成功");
                buttonQuery(null);
            } else {
                JOptionPane.showMessageDialog(this, "修改失败");
            }
        }
    }

    private void buttonDelete(ActionEvent e) {
        int currentIndex = songTable.getSelectedRow();
        if (currentIndex == -1) {
            JOptionPane.showMessageDialog(this, "请在表格中选中要删除的记录");
            return;
        }

        int n = JOptionPane.showConfirmDialog(this,
            "确定将当前记录从数据库中删除吗？",
            "确定要删除记录吗？",
            JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            int result = SongDao.delete(songs.get(currentIndex).getSongId());
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "删除成功");
                buttonQuery(null);
            } else {
                JOptionPane.showMessageDialog(this, "删除失败");
            }
        }
    }

    private void buttonEditClear(ActionEvent e) {
        titleField.setText("");
        genreField.setText("");
        artistIdField.setText("");
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private int getArtistId(String artistName) {
        switch (artistName) {
            case "Charlie XCX": return 1;
            case "Taylor Swift": return 2;
            case "Billie Eilish": return 3;
            case "Dua Lipa": return 4;
            case "The Weeknd": return 5;
            case "Beyoncé": return 6;
            case "Kanye West": return 7;
            case "Kendrick Lamar": return 8;
            default: return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User admin = new User();
            admin.setPermissions("admin");
            SongEdit frame = new SongEdit(admin);
            frame.setVisible(true);
        });
    }
} 