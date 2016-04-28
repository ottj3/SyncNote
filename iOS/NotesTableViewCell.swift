//
//  NotesTableViewCell.swift
//  SyncNote
//
//  Created by Randell Carrido on 4/28/16.
//  Copyright (c) 2016 Randell Carrido. All rights reserved.
//

import UIKit

class NotesTableViewCell: UITableViewCell {


    @IBOutlet weak var label: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
